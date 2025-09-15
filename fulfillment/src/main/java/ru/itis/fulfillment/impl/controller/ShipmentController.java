package ru.itis.fulfillment.impl.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import ru.itis.fulfillment.api.api.ShipmentApi;
import ru.itis.fulfillment.api.dto.internal.request.ChangeShipmentStatusRequest;
import ru.itis.fulfillment.api.dto.internal.request.ShipmentRequest;
import ru.itis.fulfillment.api.dto.internal.response.AllShipmentsResponse;
import ru.itis.fulfillment.api.dto.internal.response.ShipmentResponse;
import ru.itis.fulfillment.api.dto.internal.response.UpdateResponse;
import ru.itis.fulfillment.api.dto.internal.response.WarehouseResponse;
import ru.itis.fulfillment.impl.security.account.AccountPrincipal;
import ru.itis.fulfillment.impl.service.AccountService;
import ru.itis.fulfillment.impl.service.ShipmentService;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ShipmentController implements ShipmentApi {

    private final ShipmentService shipmentService;
    private final AccountService accountService;

    @Override
    public List<WarehouseResponse> getWarehouses(AccountPrincipal account) {
        String wbApiKey = accountService.getWbApiKey(account.getId()).wbApiKey();
        return shipmentService.getWarehouses(wbApiKey);
    }

    @Override
    public ShipmentResponse createShipment(AccountPrincipal account, ShipmentRequest request) {
        return shipmentService.createShipment(account.getId(), request);
    }

    @Override
    public ShipmentResponse getShipment(AccountPrincipal account, long shipmentId) {
        return shipmentService.getShipment(account.getId(), shipmentId);
    }

    @Override
    public AllShipmentsResponse getAllShipments(AccountPrincipal account, String search, String status, int page, int limit) {
        return shipmentService.getAllShipments(account.getId(), search, status, page, limit);
    }

    @Override
    public UpdateResponse changeStatus(ChangeShipmentStatusRequest request, long shipmentId) {
        return shipmentService.changeStatus(request, shipmentId);
    }
}
