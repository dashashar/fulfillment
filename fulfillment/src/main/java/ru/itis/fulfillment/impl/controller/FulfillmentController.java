package ru.itis.fulfillment.impl.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import ru.itis.fulfillment.api.api.FulfillmentApi;
import ru.itis.fulfillment.api.dto.internal.request.FulfillmentRequest;
import ru.itis.fulfillment.api.dto.internal.response.AllFulfillmentsResponse;
import ru.itis.fulfillment.api.dto.internal.response.FulfillmentResponse;
import ru.itis.fulfillment.impl.security.account.AccountPrincipal;
import ru.itis.fulfillment.impl.service.FulfillmentService;

@RestController
@RequiredArgsConstructor
public class FulfillmentController implements FulfillmentApi {

    private final FulfillmentService fulfillmentService;

    @Override
    public FulfillmentResponse createFulfillment(AccountPrincipal account, FulfillmentRequest request) {
        return fulfillmentService.createFulfillment(account.getId(), request);
    }

    @Override
    public FulfillmentResponse getFulfillment(AccountPrincipal account, long fulfillmentId) {
        return fulfillmentService.getFulfillment(fulfillmentId, account.getId());
    }

    @Override
    public AllFulfillmentsResponse getAllFulfillments(AccountPrincipal account, String search, int page, int limit) {
        return fulfillmentService.getAllFulfillments(account.getId(), search, page, limit);
    }
}
