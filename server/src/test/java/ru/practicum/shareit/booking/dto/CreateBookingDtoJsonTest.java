package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CreateBookingDtoJsonTest {

    @Autowired
    private JacksonTester<CreateBookingDto> json;

    @Test
    void testSerialize() throws Exception {
        LocalDateTime start = LocalDateTime.of(2023, 10, 1, 10, 0);
        LocalDateTime end = LocalDateTime.of(2023, 10, 1, 12, 0);

        CreateBookingDto createBookingDto = new CreateBookingDto(1L, start, end);

        JsonContent<CreateBookingDto> jsonContent = json.write(createBookingDto);

        assertThat(jsonContent).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(jsonContent).extractingJsonPathStringValue("$.start").isEqualTo("2023-10-01T10:00:00");
        assertThat(jsonContent).extractingJsonPathStringValue("$.end").isEqualTo("2023-10-01T12:00:00");
    }

    @Test
    void testDeserialize() throws Exception {
        String jsonContent = "{"
                + "  \"itemId\": 1,"
                + "  \"start\": \"2023-10-01T10:00:00\","
                + "  \"end\": \"2023-10-01T12:00:00\""
                + "}";

        LocalDateTime start = LocalDateTime.parse("2023-10-01T10:00:00");
        LocalDateTime end = LocalDateTime.parse("2023-10-01T12:00:00");

        CreateBookingDto expectedCreateBookingDto = new CreateBookingDto(1L, start, end);

        assertThat(json.parse(jsonContent)).usingRecursiveComparison().isEqualTo(expectedCreateBookingDto);
    }
}