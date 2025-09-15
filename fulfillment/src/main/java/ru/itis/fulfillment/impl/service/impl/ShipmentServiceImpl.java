package ru.itis.fulfillment.impl.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itis.fulfillment.api.dto.external.response.WbWarehouseResponse;
import ru.itis.fulfillment.api.dto.internal.request.ChangeShipmentStatusRequest;
import ru.itis.fulfillment.api.dto.internal.request.ShipmentRequest;
import ru.itis.fulfillment.api.dto.internal.response.*;
import ru.itis.fulfillment.impl.exception.*;
import ru.itis.fulfillment.impl.mapper.ShipmentMapper;
import ru.itis.fulfillment.impl.model.FulfillmentEntity;
import ru.itis.fulfillment.impl.model.ShipmentEntity;
import ru.itis.fulfillment.impl.repository.ShipmentRepository;
import ru.itis.fulfillment.impl.service.FulfillmentService;
import ru.itis.fulfillment.impl.service.GoogleSheetsService;
import ru.itis.fulfillment.impl.service.ShipmentService;
import ru.itis.fulfillment.impl.service.WildberriesApiService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShipmentServiceImpl implements ShipmentService {

    private final FulfillmentService fulfillmentService;
    private final WildberriesApiService wbService;
    private final GoogleSheetsService sheetsService;
    private final ShipmentRepository shipmentRepository;
    private final ShipmentMapper shipmentMapper;

    @Override
    public List<WarehouseResponse> getWarehouses(String wbApiKey) {
        List<WbWarehouseResponse> wbWarehouses = wbService.getWarehouses(wbApiKey);
        return shipmentMapper.toAppResponse(wbWarehouses);
    }

    @Override
    @Transactional
    public ShipmentResponse createShipment(long accountId, ShipmentRequest request) {
        try {
            FulfillmentResponse fulfillment = fulfillmentService.getFulfillment(request.fulfillmentId(), accountId);
            ShipmentEntity shipment = shipmentMapper.toEntity(request);
            shipment.setFulfillment(FulfillmentEntity.builder().id(request.fulfillmentId()).build());
            ShipmentEntity savedShipment = shipmentRepository.save(shipment);
            ShipmentResponse response = shipmentMapper.toResponse(savedShipment, fulfillment);
            String nameAccount = fulfillmentService.getAccountNameByFulfillmentId(request.fulfillmentId());
            sheetsService.insertShipmentGoogleTable(response, nameAccount);
            return response;
        } catch (GoogleSheetsException e) {
            log.error("Couldn't export to Google Sheets, transaction to save the shipment will be rolled back: account id: {} {}",
                    accountId, request);
            throw e.withUserMessage("Couldn't export to Google Sheets, so the shipment was not saved");
        } catch (DataAccessException e) {
            log.error("Couldn't save shipment accountId: {} {}, {}", accountId, request, e.getMessage(), e);
            throw new DatabaseException("Couldn't save shipment", e);
        }
    }

    @Override
    public ShipmentResponse getShipment(long accountId, long shipmentId) {
        try {
            ShipmentEntity shipment = shipmentRepository.findByIdWithFulfillment(shipmentId)
                    .orElseThrow(() -> new NotFoundException(String.format("Shipment with id: %s not found", shipmentId)));
            if (shipment.getFulfillment().getAccount().getId() != accountId) {
                log.warn("An account with id: {} was trying to access an forbidden shipment with id: {}", accountId, shipment.getFulfillment().getId());
                throw new ForbiddenException("There is no access to this shipment");
            }
            return shipmentMapper.toResponse(shipment);
        } catch (DataAccessException e) {
            log.error("Couldn't get shipment with id: {}, {}", shipmentId, e.getMessage(), e);
            throw new DatabaseException("Couldn't get shipment", e);
        }
    }

    @Override
    public AllShipmentsResponse getAllShipments(long accountId, String search, String status, int page, int limit) {
        try {
            if (limit < 1 || limit > 30 || page < 0) {
                throw new BadRequestException(limit < 1 ? "The limit must be positive" :
                        (limit > 30 ? "The limit cannot exceed 30" : "The page cannot be negative"));
            }
            Pageable pageable = PageRequest.of(page, limit, Sort.by(Sort.Direction.DESC, "date"));
            Slice<ShipmentEntity> sliceResult;
            if (search == null || search.isBlank()) {
                if (status == null || status.isBlank()) {
                    sliceResult = shipmentRepository.findByAccountId(accountId, pageable);
                } else {
                    sliceResult = shipmentRepository.findByAccountIdAndStatus(
                            accountId, status, pageable);
                }
            } else {
                String searchTerm = String.format("%s%%", search.toLowerCase().trim());
                if (status == null || status.isBlank()) {
                    sliceResult = shipmentRepository.findByAccountAndSearchTerm(accountId, searchTerm, pageable);
                } else {
                    sliceResult = shipmentRepository.findByAccountAndSearchTermAndStatus(
                            accountId, searchTerm, status, pageable);
                }
            }
            List<ShipmentResponse> shipments = shipmentMapper.toResponse(sliceResult.getContent());
            return new AllShipmentsResponse(shipments, shipments.size(), sliceResult.hasNext());
        } catch (DataAccessException e) {
            log.error("Couldn't get shipments for the account with id: {}, {}", accountId, e.getMessage(), e);
            throw new DatabaseException("Couldn't get shipments", e);
        }
    }

    @Override
    @Transactional
    public UpdateResponse changeStatus(ChangeShipmentStatusRequest request, long shipmentId) {
        try {
            ShipmentEntity shipment = shipmentRepository.findById(shipmentId)
                    .orElseThrow(() -> new NotFoundException("Shipment with id: %s not found"));
            shipment.setStatus(request.status());
            shipmentRepository.save(shipment);
            return new UpdateResponse(String.format("Successful status update on: %s", request.status()));
        } catch (DataAccessException e) {
            log.error("Couldn't change the shipment status with id: {}, {}", shipmentId, e.getMessage(), e);
            throw new DatabaseException("Couldn't change the shipment status", e);
        }
    }
}
