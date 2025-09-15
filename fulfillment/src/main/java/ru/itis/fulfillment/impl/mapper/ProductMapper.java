package ru.itis.fulfillment.impl.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.itis.fulfillment.api.dto.external.response.WbCardResponse;
import ru.itis.fulfillment.api.dto.internal.response.ProductResponse;
import ru.itis.fulfillment.impl.model.AccountEntity;
import ru.itis.fulfillment.impl.model.ProductEntity;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProductMapper {

    List<ProductResponse> toResponse(List<ProductEntity> products);

    ProductResponse toResponse(ProductEntity product);

    default List<ProductEntity> toEntity(List<WbCardResponse> response, long accountId) {
        List<ProductEntity> products = new ArrayList<>();
        if (response == null || response.isEmpty()) {
            return products;
        }
        for (WbCardResponse wbCards : response) {
            for (WbCardResponse.Card card : wbCards.getCards()) {
                String article = card.getVendorCode() != null ? card.getVendorCode() : "article is empty";
                String title = card.getTitle() != null ? card.getTitle() : "title is empty";
                String updatedAtStr = card.getUpdatedAt();
                OffsetDateTime updatedAt = updatedAtStr != null ? OffsetDateTime.parse(updatedAtStr) : OffsetDateTime.now();

                String color = "color is empty";
                if (card.getCharacteristics() != null) {
                    for (WbCardResponse.Characteristic ch : card.getCharacteristics()) {
                        if ("Цвет".equals(ch.getName())) {
                            Object value = ch.getValue();
                            if (value instanceof List<?> valueList && !valueList.isEmpty()) {
                                if (valueList.size() == 1) {
                                    color = valueList.get(0).toString();
                                } else {
                                    color = valueList.stream()
                                            .map(Object::toString)
                                            .collect(Collectors.joining(", "));
                                }
                            }
                        }
                    }
                }

                String photoUrl = "photo is empty";
                if (card.getPhotos() != null && !card.getPhotos().isEmpty() && card.getPhotos().get(0).getBig() != null && !card.getPhotos().get(0).getBig().isEmpty()) {
                    photoUrl = card.getPhotos().get(0).getBig();
                }
                if (card.getSizes() != null) {
                    for (WbCardResponse.Size sizeObj : card.getSizes()) {
                        String size = sizeObj.getTechSize() != null ? sizeObj.getTechSize() :
                                (sizeObj.getWbSize() != null ? sizeObj.getWbSize() : "0");
                        String barcode = "barcode is empty";
                        if (sizeObj.getSkus() != null && !sizeObj.getSkus().isEmpty()) {
                            barcode = String.join(", ", sizeObj.getSkus());
                        }
                        ProductEntity product = ProductEntity.builder()
                                .title(title)
                                .article(article)
                                .color(color)
                                .barcode(barcode)
                                .size(size)
                                .photoUrl(photoUrl)
                                .updatedAt(updatedAt)
                                .account(AccountEntity.builder().id(accountId).build())
                                .build();
                        products.add(product);
                    }
                } else {
                    ProductEntity product = ProductEntity.builder()
                            .title(title)
                            .article(article)
                            .color(color)
                            .barcode("barcode is empty")
                            .size("0")
                            .photoUrl(photoUrl)
                            .updatedAt(updatedAt)
                            .account(AccountEntity.builder().id(accountId).build())
                            .build();
                    products.add(product);
                }
            }
        }
        return products;
    }
}
