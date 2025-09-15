package ru.itis.fulfillment.impl.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import ru.itis.fulfillment.api.api.ProductApi;
import ru.itis.fulfillment.api.dto.internal.response.AllProductsResponse;
import ru.itis.fulfillment.api.dto.internal.response.ProductResponse;
import ru.itis.fulfillment.api.dto.internal.response.UpdateResponse;
import ru.itis.fulfillment.impl.security.account.AccountPrincipal;
import ru.itis.fulfillment.impl.service.AccountService;
import ru.itis.fulfillment.impl.service.ProductService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProductController implements ProductApi {

    private final ProductService productService;
    private final AccountService accountService;

    @Override
    public AllProductsResponse getAllProducts(AccountPrincipal account, String search, int page, int limit) {
        return productService.getProducts(account.getId(), search, page, limit);
    }

    @Override
    public UpdateResponse updateProductInformation(AccountPrincipal account) {
        String wbApiKey = accountService.getWbApiKey(account.getId()).wbApiKey();
        return productService.updateProductInformation(account.getId(), wbApiKey);
    }

    @Override
    public ProductResponse getProduct(AccountPrincipal account, long productId) {
        return productService.getProduct(account.getId(), productId);
    }
}
