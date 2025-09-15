package ru.itis.fulfillment.api.dto.external.response;

import lombok.Data;

@Data
public class WbWarehouseResponse {
    private String name;
    private Long officeId;
    private Integer id;
    private Integer cargoType;
    private Integer deliveryType;
}
