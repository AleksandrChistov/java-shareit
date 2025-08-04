package ru.practicum.shareit.share.util;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.*;

class DateTimeUtilsTest {

    @Test
    void toUTC_shouldConvertLocalDateTimeToInstant() {
        LocalDateTime localDateTime = LocalDateTime.of(2023, 10, 15, 14, 30, 45);
        ZoneId systemZone = ZoneId.systemDefault();
        Instant expectedInstant = localDateTime.atZone(systemZone).toInstant();

        Instant result = DateTimeUtils.toUTC(localDateTime);

        assertNotNull(result);
        assertEquals(expectedInstant, result);
    }

    @Test
    void toUTC_shouldConvertLocalDateTimeAtStartOfDayToInstant() {
        LocalDateTime localDateTime = LocalDateTime.of(2023, 1, 1, 0, 0, 0);
        ZoneId systemZone = ZoneId.systemDefault();
        Instant expectedInstant = localDateTime.atZone(systemZone).toInstant();

        Instant result = DateTimeUtils.toUTC(localDateTime);

        assertNotNull(result);
        assertEquals(expectedInstant, result);
    }

    @Test
    void toUTC_shouldConvertLocalDateTimeAtEndOfDayToInstant() {
        LocalDateTime localDateTime = LocalDateTime.of(2023, 12, 31, 23, 59, 59);
        ZoneId systemZone = ZoneId.systemDefault();
        Instant expectedInstant = localDateTime.atZone(systemZone).toInstant();

        Instant result = DateTimeUtils.toUTC(localDateTime);

        assertNotNull(result);
        assertEquals(expectedInstant, result);
    }

    @Test
    void toLocalDateTime_shouldConvertInstantToLocalDateTime() {
        ZoneId systemZone = ZoneId.systemDefault();
        Instant instant = LocalDateTime.of(2023, 10, 15, 14, 30, 45)
                .atZone(systemZone).toInstant();
        LocalDateTime expectedLocalDateTime = LocalDateTime.ofInstant(instant, systemZone);

        LocalDateTime result = DateTimeUtils.toLocalDateTime(instant);

        assertNotNull(result);
        assertEquals(expectedLocalDateTime, result);
    }

    @Test
    void toLocalDateTime_shouldConvertInstantAtStartOfDayToLocalDateTime() {
        ZoneId systemZone = ZoneId.systemDefault();
        Instant instant = LocalDateTime.of(2023, 1, 1, 0, 0, 0)
                .atZone(systemZone).toInstant();
        LocalDateTime expectedLocalDateTime = LocalDateTime.ofInstant(instant, systemZone);

        LocalDateTime result = DateTimeUtils.toLocalDateTime(instant);

        assertNotNull(result);
        assertEquals(expectedLocalDateTime, result);
    }

    @Test
    void toLocalDateTime_shouldConvertInstantAtEndOfDayToLocalDateTime() {
        ZoneId systemZone = ZoneId.systemDefault();
        Instant instant = LocalDateTime.of(2023, 12, 31, 23, 59, 59)
                .atZone(systemZone).toInstant();
        LocalDateTime expectedLocalDateTime = LocalDateTime.ofInstant(instant, systemZone);

        LocalDateTime result = DateTimeUtils.toLocalDateTime(instant);

        assertNotNull(result);
        assertEquals(expectedLocalDateTime, result);
    }

    @Test
    void toUTCAndToLocalDateTime_shouldBeConsistent() {
        LocalDateTime originalDateTime = LocalDateTime.of(2023, 10, 15, 14, 30, 45);

        Instant instant = DateTimeUtils.toUTC(originalDateTime);
        LocalDateTime resultDateTime = DateTimeUtils.toLocalDateTime(instant);

        assertEquals(originalDateTime, resultDateTime);
    }

    @Test
    void toLocalDateTimeAndToUTC_shouldBeConsistent() {
        ZoneId systemZone = ZoneId.systemDefault();
        Instant originalInstant = LocalDateTime.of(2023, 10, 15, 14, 30, 45)
                .atZone(systemZone).toInstant();

        LocalDateTime localDateTime = DateTimeUtils.toLocalDateTime(originalInstant);
        Instant resultInstant = DateTimeUtils.toUTC(localDateTime);

        assertEquals(originalInstant, resultInstant);
    }

}