package ru.itis.fulfillment.impl.service;

import ru.itis.fulfillment.api.dto.internal.response.FulfillmentResponse;
import ru.itis.fulfillment.api.dto.internal.response.ShipmentResponse;

import java.util.List;

public interface GoogleSheetsService {

    void insertRowAtTop(String sheetName, List<Object> rowData, int startRowIndex);

    void insertFulfillmentGoogleTable(FulfillmentResponse fulfillment, String name);

    void insertShipmentGoogleTable(ShipmentResponse shipment, String name);
}
