package ru.itis.fulfillment.impl.service;

import org.springframework.web.bind.annotation.PathVariable;
import ru.itis.fulfillment.api.dto.internal.request.ChangeShipmentStatusRequest;
import ru.itis.fulfillment.api.dto.internal.request.ShipmentRequest;
import ru.itis.fulfillment.api.dto.internal.response.AllShipmentsResponse;
import ru.itis.fulfillment.api.dto.internal.response.ShipmentResponse;
import ru.itis.fulfillment.api.dto.internal.response.UpdateResponse;
import ru.itis.fulfillment.api.dto.internal.response.WarehouseResponse;

import java.util.List;

public interface ShipmentService {

    List<WarehouseResponse> getWarehouses(String wbApiKey);

    ShipmentResponse createShipment(long accountId, ShipmentRequest request);

    ShipmentResponse getShipment(long accountId, long shipmentId);

    AllShipmentsResponse getAllShipments(long accountId, String search, String status, int page, int limit);

    UpdateResponse changeStatus(ChangeShipmentStatusRequest request, long shipmentId);

}
