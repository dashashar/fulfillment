package ru.itis.fulfillment.impl.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.itis.fulfillment.api.dto.external.response.WbCardResponse;
import ru.itis.fulfillment.api.dto.internal.response.AllProductsResponse;
import ru.itis.fulfillment.api.dto.internal.response.ProductResponse;
import ru.itis.fulfillment.api.dto.internal.response.UpdateResponse;
import ru.itis.fulfillment.impl.exception.BadRequestException;
import ru.itis.fulfillment.impl.exception.DatabaseException;
import ru.itis.fulfillment.impl.exception.ForbiddenException;
import ru.itis.fulfillment.impl.exception.NotFoundException;
import ru.itis.fulfillment.impl.mapper.ProductMapper;
import ru.itis.fulfillment.impl.model.ProductEntity;
import ru.itis.fulfillment.impl.repository.ProductRepository;
import ru.itis.fulfillment.impl.service.ProductService;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final WildberriesApiServiceImpl wbService;

    @Override
    public AllProductsResponse getProducts(long accountId, String search, int page, int limit) {
        try {
            if (limit < 1 || limit > 30 || page < 0) {
                throw new BadRequestException(limit < 1 ? "The limit must be positive" :
                        (limit > 30 ? "The limit cannot exceed 30" : "The page cannot be negative"));
            }
            Pageable pageable = PageRequest.of(page, limit, Sort.by("id"));
            Slice<ProductEntity> sliceResult;
            if (search == null || search.isBlank()) {
                sliceResult = productRepository.findByAccount_Id(accountId, pageable);
            } else {
                String searchTerm = String.format("%s%%", search.toLowerCase().trim());
                sliceResult = productRepository.findByAccountAndSearchTerm(
                        accountId, searchTerm, pageable);
            }
            List<ProductResponse> products = productMapper.toResponse(sliceResult.getContent());
            return new AllProductsResponse(products, products.size(), sliceResult.hasNext());
        } catch (DataAccessException e) {
            log.error("Couldn't get products for the account with id: {}, {}", accountId, e.getMessage(), e);
            throw new DatabaseException("Couldn't get products", e);
        }
    }

    @Override
    @Transactional
    public UpdateResponse updateProductInformation(long accountId, String wbApiKey) {
        try {
            List<WbCardResponse> wbCards = wbService.getAllProducts(wbApiKey);
            List<ProductEntity> newProducts = productMapper.toEntity(wbCards, accountId);
            List<ProductEntity> dbProducts = productRepository.findAllByAccount_Id(accountId);
            if (dbProducts.isEmpty()) {
                if (newProducts.isEmpty()) {
                    return new UpdateResponse("No products were found for the upgrade");
                } else {
                    productRepository.saveAll(newProducts);
                    return new UpdateResponse("Successfully updated the products");
                }
            } else if (newProducts.isEmpty()) {
                productRepository.deleteAllByAccount_Id(accountId);
                return new UpdateResponse("Successfully updated the products");
            }
            Map<String, ProductEntity> dbProductMap = dbProducts.stream()
                    .collect(Collectors.toMap(ProductEntity::getBarcode, p -> p));

            Map<String, ProductEntity> newProductMap = newProducts.stream()
                    .collect(Collectors.toMap(ProductEntity::getBarcode, p -> p, (a, b) -> a));

            List<ProductEntity> toSave = new ArrayList<>();

            for (ProductEntity newProduct : newProducts) {
                ProductEntity dbProduct = dbProductMap.get(newProduct.getBarcode());
                if (dbProduct != null) {
                    if (newProduct.getUpdatedAt() != null && dbProduct.getUpdatedAt() != null &&
                            newProduct.getUpdatedAt().isAfter(dbProduct.getUpdatedAt())) {
                        dbProduct.setTitle(newProduct.getTitle());
                        dbProduct.setArticle(newProduct.getArticle());
                        dbProduct.setSize(newProduct.getSize());
                        dbProduct.setColor(newProduct.getColor());
                        dbProduct.setPhotoUrl(newProduct.getPhotoUrl());
                        dbProduct.setUpdatedAt(newProduct.getUpdatedAt());
                        toSave.add(dbProduct);
                    }
                } else {
                    toSave.add(newProduct);
                }
            }
            if (!toSave.isEmpty()) {
                productRepository.saveAll(toSave);
            }
            Set<String> newBarcodes = newProductMap.keySet();
            Set<String> dbBarcodes = dbProductMap.keySet();
            dbBarcodes.removeAll(newBarcodes);
            if (!dbBarcodes.isEmpty()) {
                productRepository.deleteByAccount_IdAndBarcodeIn(accountId, new ArrayList<>(dbBarcodes));
            }
            return new UpdateResponse("Successfully updated the products");
        } catch (DataAccessException e) {
            log.error("Couldn't update product information for the account with id: {}, {}", accountId, e.getMessage(), e);
            throw new DatabaseException("Couldn't update product information", e);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void savingProductsAfterRegistration(String wbApiKey, long accountId) {
        try {
            List<WbCardResponse> wbCards = wbService.getAllProducts(wbApiKey);
            List<ProductEntity> newProducts = productMapper.toEntity(wbCards, accountId);
            if (newProducts != null && !newProducts.isEmpty()) {
                productRepository.saveAll(newProducts);
            }
        } catch (DataAccessException e) {
            log.error("Couldn't save products after registration: {}", e.getMessage(), e);
            throw new DatabaseException("Couldn't save products", e);
        }
    }

    @Override
    public ProductResponse getProduct(long accountId, long productId) {
        try {
            ProductEntity productEntity = productRepository.findById(productId)
                    .orElseThrow(() -> new NotFoundException(String.format("Product with id: %s not found", productId)));
            if (productEntity.getAccount().getId() != accountId) {
                log.warn("An account with id: {} was trying to access an forbidden product with id: {}", accountId, productId);
                throw new ForbiddenException("There is no access to this product");
            }
            return productMapper.toResponse(productEntity);
        } catch (DataAccessException e) {
            log.error("Couldn't get product with id: {}, {}", productId, e.getMessage(), e);
            throw new DatabaseException("Couldn't get product", e);
        }
    }
}
