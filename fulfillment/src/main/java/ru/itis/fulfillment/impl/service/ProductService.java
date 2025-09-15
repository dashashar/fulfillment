package ru.itis.fulfillment.impl.service;

import ru.itis.fulfillment.api.dto.internal.response.AllProductsResponse;
import ru.itis.fulfillment.api.dto.internal.response.ProductResponse;
import ru.itis.fulfillment.api.dto.internal.response.UpdateResponse;

public interface ProductService {

    AllProductsResponse getProducts(long accountId, String search, int page, int limit);

    UpdateResponse updateProductInformation(long accountId, String wbApiKey);

    void savingProductsAfterRegistration(String wbApiKey, long accountId);

    ProductResponse getProduct(long accountId, long productId);

}
