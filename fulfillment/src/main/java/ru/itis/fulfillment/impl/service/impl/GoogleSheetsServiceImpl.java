package ru.itis.fulfillment.impl.service.impl;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.itis.fulfillment.api.dto.internal.response.FulfillmentResponse;
import ru.itis.fulfillment.api.dto.internal.response.ShipmentResponse;
import ru.itis.fulfillment.impl.exception.GoogleSheetsException;
import ru.itis.fulfillment.impl.service.GoogleSheetsService;
import ru.itis.fulfillment.impl.util.DateTimeUtils;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Slf4j
public class GoogleSheetsServiceImpl implements GoogleSheetsService {

    private final Sheets sheetsService;
    private final String spreadsheetId;
    private final String fulfillmentSheetName;
    private final String shipmentSheetName;
    private final Map<String, Integer> sheetIdsCache = new HashMap<>();

    public GoogleSheetsServiceImpl(@Value("${google.spreadsheet.id}") String spreadsheetId,
                                   @Value("${google.spreadsheet.sheet.name.fulfillment}") String fulfillmentSheetName,
                                   @Value("${google.spreadsheet.sheet.name.shipment}") String shipmentSheetName,
                                   Sheets sheetsService) throws Exception {
        this.spreadsheetId = spreadsheetId;
        this.fulfillmentSheetName = fulfillmentSheetName;
        this.shipmentSheetName = shipmentSheetName;
        this.sheetsService = sheetsService;
    }

    private Integer getSheetId(String sheetName) throws IOException {
        if (sheetIdsCache.containsKey(sheetName)) {
            return sheetIdsCache.get(sheetName);
        }
        Spreadsheet spreadsheet = sheetsService.spreadsheets().get(spreadsheetId).execute();
        for (Sheet sheet : spreadsheet.getSheets()) {
            String currentSheetName = sheet.getProperties().getTitle();
            sheetIdsCache.put(currentSheetName, sheet.getProperties().getSheetId());
        }
        if (!sheetIdsCache.containsKey(sheetName)) {
            log.error("Sheet in the Google table with the name {} was not found", sheetName);
            throw new IllegalArgumentException(String.format("Sheet not found: %s", sheetName));
        }
        return sheetIdsCache.get(sheetName);
    }

    @Override
    public void insertRowAtTop(String sheetName, List<Object> rowData, int startRowIndex) {
        try {
            Integer sheetId = getSheetId(sheetName);
            Request insertRowRequest = new Request().setInsertDimension(
                    new InsertDimensionRequest()
                            .setRange(new DimensionRange()
                                    .setSheetId(sheetId)
                                    .setDimension("ROWS")
                                    .setStartIndex(startRowIndex)
                                    .setEndIndex(startRowIndex + 1)
                            )
                            .setInheritFromBefore(false)
            );
            List<CellData> cellData = new ArrayList<>();
            for (Object value : rowData) {
                cellData.add(new CellData().setUserEnteredValue(
                        new ExtendedValue().setStringValue(value == null ? "" : value.toString())
                ));
            }
            RowData row = new RowData().setValues(cellData);
            Request updateCellsRequest = new Request().setUpdateCells(
                    new UpdateCellsRequest()
                            .setStart(new GridCoordinate()
                                    .setSheetId(sheetId)
                                    .setRowIndex(startRowIndex)
                                    .setColumnIndex(0)
                            )
                            .setRows(Collections.singletonList(row))
                            .setFields("userEnteredValue")
            );

            BatchUpdateSpreadsheetRequest batchRequest = new BatchUpdateSpreadsheetRequest()
                    .setRequests(Arrays.asList(insertRowRequest, updateCellsRequest));

            sheetsService.spreadsheets().batchUpdate(spreadsheetId, batchRequest).execute();
            log.debug("Successfully inserted row at top of sheet '{}'", sheetName);
            //TODO добавить ретрай при 429
        } catch (IOException e) {
            log.error("Google Sheets API error while inserting row: {}", e.getMessage());
            throw new GoogleSheetsException("Google Sheets API error while inserting row", e);
        } catch (Exception e) {
            log.error("Unexpected error while inserting row: {}", e.getMessage(), e);
            throw new GoogleSheetsException("Unexpected error while inserting row: " + e.getMessage(), e);
        }
    }

    @Override
    public void insertFulfillmentGoogleTable(FulfillmentResponse fulfillment, String name) {
        List<Object> rowData = Arrays.asList(
                name,
                DateTimeUtils.formatToMoscowTimeWithZone(OffsetDateTime.parse(fulfillment.date())),
                fulfillment.photoUrl(),
                fulfillment.title(),
                fulfillment.quantity(),
                "",
                "",
                "",
                "",
                fulfillment.barcode(),
                fulfillment.article(),
                fulfillment.size(),
                fulfillment.color(),
                fulfillment.taskDescription());
        insertRowAtTop(fulfillmentSheetName, rowData, 2);
    }

    @Override
    public void insertShipmentGoogleTable(ShipmentResponse shipment, String name) {
        List<Object> rowData = Arrays.asList(
                name,
                DateTimeUtils.formatToMoscowTimeWithZone(OffsetDateTime.parse(shipment.date())),
                shipment.title(),
                shipment.size(),
                shipment.color(),
                shipment.barcode(),
                shipment.quantity(),
                "",
                "",
                shipment.warehouseName(),
                shipment.warehouseId(),
                shipment.id());
        insertRowAtTop(shipmentSheetName, rowData, 1);
    }

}
