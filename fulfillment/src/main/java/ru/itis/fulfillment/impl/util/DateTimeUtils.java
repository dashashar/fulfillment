package ru.itis.fulfillment.impl.util;

import lombok.experimental.UtilityClass;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@UtilityClass
public class DateTimeUtils {

    private final DateTimeFormatter customDateTimeWithZone = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy XXX");
    private final DateTimeFormatter iso8601WithShortMillis = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSX");

    public String formatToMoscowTimeWithZone(OffsetDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        ZonedDateTime moscowTime = dateTime.atZoneSameInstant(ZoneId.of("Europe/Moscow"));
        return moscowTime.format(customDateTimeWithZone);
    }

    public String formatToIsoWithShortMillis(OffsetDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(iso8601WithShortMillis);
    }
}
