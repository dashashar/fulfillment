package ru.itis.fulfillment.impl.service;

import ru.itis.fulfillment.api.dto.internal.request.FulfillmentRequest;
import ru.itis.fulfillment.api.dto.internal.response.AllFulfillmentsResponse;
import ru.itis.fulfillment.api.dto.internal.response.FulfillmentResponse;

public interface FulfillmentService {

    FulfillmentResponse createFulfillment(long accountId, FulfillmentRequest request);

    FulfillmentResponse getFulfillment(long fulfillmentId, long accountId);

    AllFulfillmentsResponse getAllFulfillments(long accountId, String search, int page, int limit);

    String getAccountNameByFulfillmentId(long fulfillmentId);

}
