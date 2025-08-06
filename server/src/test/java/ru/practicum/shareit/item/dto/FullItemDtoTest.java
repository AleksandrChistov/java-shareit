package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class FullItemDtoTest {
    @Autowired
    private JacksonTester<FullItemDto> json;

    @Test
    void testSerialize() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        FullItemDto fullItemDto = new FullItemDto(
                1L,
                "Дрель",
                "Простая дрель",
                true,
                now,
                now.plusSeconds(3600),
                1L,
                List.of()
        );

        JsonContent<FullItemDto> jsonContent = json.write(fullItemDto);

        assertThat(jsonContent).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(jsonContent).extractingJsonPathStringValue("$.name").isEqualTo("Дрель");
        assertThat(jsonContent).extractingJsonPathStringValue("$.description").isEqualTo("Простая дрель");
        assertThat(jsonContent).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(jsonContent).extractingJsonPathNumberValue("$.requestId").isEqualTo(1);
        assertThat(jsonContent).extractingJsonPathStringValue("$.lastBooking").isNotNull();
        assertThat(jsonContent).extractingJsonPathStringValue("$.nextBooking").isNotNull();
        assertThat(jsonContent).extractingJsonPathArrayValue("$.comments").isEmpty();
    }

    @Test
    void testDeserialize() throws Exception {
        String jsonContentStr = "{\n" +
                "  \"id\": 1,\n" +
                "  \"name\": \"Дрель\",\n" +
                "  \"description\": \"Простая дрель\",\n" +
                "  \"available\": true,\n" +
                "  \"requestId\": 1,\n" +
                "  \"lastBooking\": \"2023-10-01T10:00:00Z\",\n" +
                "  \"nextBooking\": \"2023-10-01T12:00:00Z\",\n" +
                "  \"comments\": []\n" +
                "}";

        var parsedResult = json.parse(jsonContentStr);

        assertThat(parsedResult).isNotNull();
        FullItemDto fullItemDto = parsedResult.getObject();

        assertThat(fullItemDto.getId()).isEqualTo(1L);
        assertThat(fullItemDto.getName()).isEqualTo("Дрель");
        assertThat(fullItemDto.getDescription()).isEqualTo("Простая дрель");
        assertThat(fullItemDto.getAvailable()).isEqualTo(true);
        assertThat(fullItemDto.getRequestId()).isEqualTo(1L);
        assertThat(fullItemDto.getLastBooking()).isNotNull();
        assertThat(fullItemDto.getNextBooking()).isNotNull();
        assertThat(fullItemDto.getComments()).isEmpty();
    }
}