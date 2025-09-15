package ru.itis.fulfillment.impl.service;

import ru.itis.fulfillment.api.dto.external.response.WbCardResponse;
import ru.itis.fulfillment.api.dto.external.response.WbWarehouseResponse;

import java.util.List;

public interface WildberriesApiService {

    List<WbWarehouseResponse> getWarehouses(String apiKey);

    List<WbCardResponse> getAllProducts(String apiKey);
}
