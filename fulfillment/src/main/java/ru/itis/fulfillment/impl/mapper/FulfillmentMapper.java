package ru.itis.fulfillment.impl.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import ru.itis.fulfillment.api.dto.internal.request.FulfillmentRequest;
import ru.itis.fulfillment.api.dto.internal.response.FulfillmentResponse;
import ru.itis.fulfillment.api.dto.internal.response.ProductResponse;
import ru.itis.fulfillment.impl.model.FulfillmentEntity;
import ru.itis.fulfillment.impl.util.DateTimeUtils;

import java.time.OffsetDateTime;
import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface FulfillmentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "account", ignore = true)
    @Mapping(target = "quantity", source = "request.quantity")
    @Mapping(target = "taskDescription", source = "request.taskDescription")
    @Mapping(target = "title", source = "product.title")
    @Mapping(target = "barcode", source = "product.barcode")
    @Mapping(target = "article", source = "product.article")
    @Mapping(target = "size", source = "product.size")
    @Mapping(target = "color", source = "product.color")
    @Mapping(target = "photoUrl", source = "product.photoUrl")
    @Mapping(target = "date", expression = "java(java.time.OffsetDateTime.now(java.time.ZoneOffset.UTC))")
    FulfillmentEntity toEntity(FulfillmentRequest request, ProductResponse product);

    @Mapping(target = "date", source = "date", qualifiedByName = "formatDate")
    FulfillmentResponse toResponse(FulfillmentEntity entity);

    List<FulfillmentResponse> toResponse(List<FulfillmentEntity> entity);

    @Named("formatDate")
    default String formatDate(OffsetDateTime dateTime) {
        return DateTimeUtils.formatToIsoWithShortMillis(dateTime);
    }
}
