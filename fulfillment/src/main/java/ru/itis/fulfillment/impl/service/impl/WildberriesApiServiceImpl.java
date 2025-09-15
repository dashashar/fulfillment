package ru.itis.fulfillment.impl.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import ru.itis.fulfillment.api.dto.external.request.WbProductFilterRequest;
import ru.itis.fulfillment.api.dto.external.response.WbWarehouseResponse;
import ru.itis.fulfillment.api.dto.external.response.WbCardResponse;
import ru.itis.fulfillment.impl.exception.WildberriesApiException;
import ru.itis.fulfillment.impl.service.WildberriesApiService;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class WildberriesApiServiceImpl implements WildberriesApiService {

    public static final String GET_WAREHOUSES_URL = "https://marketplace-api.wildberries.ru/api/v3/warehouses";
    public static final String GET_CARDS_URL = "https://content-api.wildberries.ru/content/v2/get/cards/list";

    private final RestTemplate restTemplate;

    @Override
    public List<WbWarehouseResponse> getWarehouses(String apiKey) {
        HttpHeaders headers = getHeaders(apiKey);
        try {
            ResponseEntity<List<WbWarehouseResponse>> response = restTemplate.exchange(
                    GET_WAREHOUSES_URL,
                    HttpMethod.GET, new HttpEntity<>(headers),
                    new ParameterizedTypeReference<>(){});
            return response.getBody() != null ? response.getBody() : Collections.emptyList();
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            log.error("Request to get warehouses to the Wildberries API returned an error: {}",
                    e.getResponseBodyAsString());
            throw new WildberriesApiException("Wildberries API Error", e);
        }
    }

    @Override
    public List<WbCardResponse> getAllProducts(String apiKey) {
        try {
            List<WbCardResponse> cardResponses = new ArrayList<>();
            boolean hasMore = true;
            String updatedAt = null;
            Integer nmID = null;
            int limit = 100;
            while (hasMore) {
                HttpHeaders headers = getHeaders(apiKey);
                WbProductFilterRequest requestBody = getProductFilterRequest(updatedAt, nmID, limit);
                ResponseEntity<WbCardResponse> response = restTemplate.exchange(
                        GET_CARDS_URL,
                        HttpMethod.POST, new HttpEntity<>(requestBody, headers), WbCardResponse.class);
                WbCardResponse wbCard = response.getBody();
                if (wbCard == null || wbCard.getCards() == null || wbCard.getCards().isEmpty()) {
                    break;
                }
                cardResponses.add(wbCard);
                if (wbCard.getCursor().getTotal() < limit) {
                    hasMore = false;
                } else {
                    updatedAt = wbCard.getCursor().getUpdatedAt();
                    nmID = wbCard.getCursor().getNmID();
                }
            }
            return cardResponses;
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            log.error("Request to get products to the Wildberries API returned an error: {}", e.getResponseBodyAsString());
            throw new WildberriesApiException("Wildberries API Error", e);
        }
    }

    private HttpHeaders getHeaders(String apiKey) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private WbProductFilterRequest getProductFilterRequest(String updatedAt, Integer nmID, int limit) {
        WbProductFilterRequest requestBody = WbProductFilterRequest.builder()
                .settings(
                        WbProductFilterRequest.Settings.builder()
                                .sort(WbProductFilterRequest.Sort.builder().ascending(false).build())
                                .filter(WbProductFilterRequest.Filter.builder().withPhoto(-1).build())
                                .cursor(WbProductFilterRequest.Cursor.builder().limit(limit).build())
                                .build())
                .build();
        if (updatedAt != null && nmID != null) {
            WbProductFilterRequest.Cursor cursor = requestBody.getSettings().getCursor();
            cursor.setUpdatedAt(updatedAt);
            cursor.setNmID(nmID);
        }
        return requestBody;
    }
}

