package ru.itis.fulfillment.api.dto.external.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WbProductFilterRequest {
    private Settings settings;

    @Data
    @Builder
    public static class Settings {
        private Sort sort;
        private Filter filter;
        private Cursor cursor;
    }

    @Data
    @Builder
    public static class Sort {
        private boolean ascending;
    }

    @Data
    @Builder
    public static class Filter {
        private int withPhoto;
    }

    @Data
    @Builder
    public static class Cursor {
        private String updatedAt;
        private Integer nmID;
        private Integer limit;
    }
}
