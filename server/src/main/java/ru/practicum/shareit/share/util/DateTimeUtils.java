package ru.practicum.shareit.share.util;

import lombok.experimental.UtilityClass;

import java.time.*;

/**
 * Class for converting between LocalDateTime and Instant.
 * Uses the default JVM time zone for conversion.
 */
@UtilityClass
public class DateTimeUtils {
    /**
     * Converts a LocalDateTime to an Instant in UTC.
     * Assumes that the provided LocalDateTime is in the default JVM time zone.
     *
     * @param localDateTime the LocalDateTime in the default JVM time zone
     * @return the corresponding Instant in UTC
     */
    public static Instant toUTC(LocalDateTime localDateTime) {
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant();
    }

    /**
     * Converts an Instant to a LocalDateTime in the default JVM time zone.
     *
     * @param instant the Instant to convert
     * @return the corresponding LocalDateTime in the default JVM time zone
     */
    public static LocalDateTime toLocalDateTime(Instant instant) {
        ZoneId defaultZoneId = ZoneId.systemDefault();
        return LocalDateTime.ofInstant(instant, defaultZoneId);
    }
}
