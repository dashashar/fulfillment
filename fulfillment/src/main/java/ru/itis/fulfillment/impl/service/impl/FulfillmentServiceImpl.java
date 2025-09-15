package ru.itis.fulfillment.impl.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itis.fulfillment.api.dto.internal.request.FulfillmentRequest;
import ru.itis.fulfillment.api.dto.internal.response.AllFulfillmentsResponse;
import ru.itis.fulfillment.api.dto.internal.response.FulfillmentResponse;
import ru.itis.fulfillment.api.dto.internal.response.ProductResponse;
import ru.itis.fulfillment.impl.exception.*;
import ru.itis.fulfillment.impl.mapper.FulfillmentMapper;
import ru.itis.fulfillment.impl.model.AccountEntity;
import ru.itis.fulfillment.impl.model.FulfillmentEntity;
import ru.itis.fulfillment.impl.repository.FulfillmentRepository;
import ru.itis.fulfillment.impl.service.FulfillmentService;
import ru.itis.fulfillment.impl.service.GoogleSheetsService;
import ru.itis.fulfillment.impl.service.ProductService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FulfillmentServiceImpl implements FulfillmentService {

    private final FulfillmentRepository fulfillmentRepository;
    private final FulfillmentMapper fulfillmentMapper;
    private final ProductService productService;
    private final GoogleSheetsService sheetsService;

    @Override
    @Transactional
    public FulfillmentResponse createFulfillment(long accountId, FulfillmentRequest request) {
        try {
            ProductResponse product = productService.getProduct(accountId, request.productId());
            FulfillmentEntity fulfillment = fulfillmentMapper.toEntity(request, product);
            fulfillment.setAccount(AccountEntity.builder().id(accountId).build());
            FulfillmentEntity saved = fulfillmentRepository.save(fulfillment);
            FulfillmentResponse response = fulfillmentMapper.toResponse(saved);
            String nameAccount = getAccountNameByFulfillmentId(saved.getId());
            sheetsService.insertFulfillmentGoogleTable(response, nameAccount);
            return response;
        } catch (GoogleSheetsException e) {
            log.error("Couldn't export to Google Sheets, transaction to save the fulfillment will be rolled back: account id: {} {}",
                    accountId, request);
            throw e.withUserMessage("Couldn't export to Google Sheets, so the fulfillment was not saved");
        } catch (DataAccessException e) {
            log.error("Couldn't save fulfillment account id: {} {}, {}", accountId, request, e.getMessage(), e);
            throw new DatabaseException("Couldn't save fulfillment", e);
        }
    }

    @Override
    public FulfillmentResponse getFulfillment(long fulfillmentId, long accountId) {
        try {
            FulfillmentEntity fulfillment = fulfillmentRepository.findById(fulfillmentId)
                    .orElseThrow(() -> new NotFoundException(String.format("Fulfillment with id: %s not found", fulfillmentId)));
            if (fulfillment.getAccount().getId() != accountId) {
                log.warn("An account with id: {} was trying to access an forbidden fulfillment with id: {}", accountId, fulfillmentId);
                throw new ForbiddenException("There is no access to this fulfillment");
            }
            return fulfillmentMapper.toResponse(fulfillment);
        } catch (DataAccessException e) {
            log.error("Couldn't get fulfillment with id: {}, {}", fulfillmentId, e.getMessage(), e);
            throw new DatabaseException("Couldn't get fulfillment", e);
        }
    }

    @Override
    public AllFulfillmentsResponse getAllFulfillments(long accountId, String search, int page, int limit) {
        try {
            if (limit < 1 || limit > 30 || page < 0) {
                throw new BadRequestException(limit < 1 ? "The limit must be positive" :
                        (limit > 30 ? "The limit cannot exceed 30" : "The page cannot be negative"));
            }
            Pageable pageable = PageRequest.of(page, limit, Sort.by(Sort.Direction.DESC, "date"));
            Slice<FulfillmentEntity> sliceResult;
            if (search == null || search.isBlank()) {
                sliceResult = fulfillmentRepository.findByAccount_Id(accountId, pageable);
            } else {
                String searchTerm = String.format("%s%%", search.toLowerCase().trim());
                sliceResult = fulfillmentRepository.findByAccountAndSearchTerm(
                        accountId, searchTerm, pageable);
            }
            List<FulfillmentResponse> fulfillments = fulfillmentMapper.toResponse(sliceResult.getContent());
            return new AllFulfillmentsResponse(fulfillments, fulfillments.size(), sliceResult.hasNext());
        } catch (DataAccessException e) {
            log.error("Couldn't get fulfillments for the account with id: {}, {}", accountId, e.getMessage(), e);
            throw new DatabaseException("Couldn't get fulfillments", e);
        }
    }

    @Override
    public String getAccountNameByFulfillmentId(long fulfillmentId) {
        Optional<String> nameOpt = fulfillmentRepository.findAccountNameByFulfillmentId(fulfillmentId);
        if (nameOpt.isEmpty()){
            throw new NotFoundException(String.format("Name account not found with fulfillment id: %s", fulfillmentId));
        }
        return nameOpt.get();
    }

}
