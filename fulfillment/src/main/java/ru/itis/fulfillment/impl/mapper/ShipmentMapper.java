package ru.itis.fulfillment.impl.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import ru.itis.fulfillment.api.dto.external.response.WbWarehouseResponse;
import ru.itis.fulfillment.api.dto.internal.request.ShipmentRequest;
import ru.itis.fulfillment.api.dto.internal.response.FulfillmentResponse;
import ru.itis.fulfillment.api.dto.internal.response.ShipmentResponse;
import ru.itis.fulfillment.api.dto.internal.response.WarehouseResponse;
import ru.itis.fulfillment.impl.model.ShipmentEntity;
import ru.itis.fulfillment.impl.util.DateTimeUtils;

import java.time.OffsetDateTime;
import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ShipmentMapper {

    List<WarehouseResponse> toAppResponse(List<WbWarehouseResponse> wbResponse);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fulfillment", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "date", expression = "java(java.time.OffsetDateTime.now(java.time.ZoneOffset.UTC))")
    ShipmentEntity toEntity(ShipmentRequest wbResponse);

    @Mapping(target = "fulfillmentId", source = "entity.fulfillment.id")
    @Mapping(target = "date", source = "date", qualifiedByName = "formatDate")
    @Mapping(target = "title", source = "entity.fulfillment.title")
    @Mapping(target = "barcode", source = "entity.fulfillment.barcode")
    @Mapping(target = "article", source = "entity.fulfillment.article")
    @Mapping(target = "size", source = "entity.fulfillment.size")
    @Mapping(target = "color", source = "entity.fulfillment.color")
    ShipmentResponse toResponse(ShipmentEntity entity);

    default ShipmentResponse toResponse(ShipmentEntity entity, FulfillmentResponse fulfillment) {
        if (entity == null) {
            return null;
        }
        String title = null;
        String barcode = null;
        String article = null;
        String size = null;
        String color = null;
        if (fulfillment != null) {
            title = fulfillment.title();
            barcode = fulfillment.barcode();
            article = fulfillment.article();
            size = fulfillment.size();
            color = fulfillment.color();
        }
        return new ShipmentResponse(entity.getId(), DateTimeUtils.formatToIsoWithShortMillis(entity.getDate()),
                entity.getQuantity(), entity.getWarehouseId(), entity.getWarehouseName(), entity.getStatus(),
                entity.getFulfillment().getId(), title, barcode, article, size, color);
    }

    List<ShipmentResponse> toResponse(List<ShipmentEntity> entity);

    @Named("formatDate")
    default String formatDate(OffsetDateTime dateTime) {
        return DateTimeUtils.formatToIsoWithShortMillis(dateTime);
    }

}
