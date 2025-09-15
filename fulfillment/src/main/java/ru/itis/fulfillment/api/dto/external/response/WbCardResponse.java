package ru.itis.fulfillment.api.dto.external.response;

import lombok.Data;

import java.util.List;

@Data
public class WbCardResponse {

    private List<Card> cards;
    private Cursor cursor;

    @Data
    public static class Card {
        private String vendorCode;
        private String title;
        private List<Size> sizes;
        private List<Photo> photos;
        private List<Characteristic> characteristics;
        private String updatedAt;
    }

    @Data
    public static class Size {
        private String techSize;
        private String wbSize;
        private List<String> skus;
    }

    @Data
    public static class Photo {
        private String big;
    }

    @Data
    public static class Characteristic {
        private String name;
        private Object value;
    }

    @Data
    public static class Cursor {
        private String updatedAt;
        private Integer nmID;
        private Integer total;
    }
}
