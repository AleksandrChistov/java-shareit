package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoJsonTest {

    @Autowired
    private JacksonTester<BookingDto> json;

    @Test
    void testSerialize() throws Exception {
        UserDto bookerDto = new UserDto(1L, "Booker Name", "booker@example.com");
        ItemDto itemDto = new ItemDto(1L, "Item Name", "Item Description", true, null);

        LocalDateTime start = LocalDateTime.of(2023, 10, 1, 10, 0);
        LocalDateTime end = LocalDateTime.of(2023, 10, 1, 12, 0);

        BookingDto bookingDto = new BookingDto(1L, start, end, BookingStatus.WAITING, bookerDto, itemDto);

        JsonContent<BookingDto> jsonContent = json.write(bookingDto);

        assertThat(jsonContent).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(jsonContent).extractingJsonPathStringValue("$.start").isEqualTo("2023-10-01T10:00:00");
        assertThat(jsonContent).extractingJsonPathStringValue("$.end").isEqualTo("2023-10-01T12:00:00");
        assertThat(jsonContent).extractingJsonPathStringValue("$.status").isEqualTo("WAITING");
        assertThat(jsonContent).extractingJsonPathNumberValue("$.booker.id").isEqualTo(1);
        assertThat(jsonContent).extractingJsonPathStringValue("$.booker.name").isEqualTo("Booker Name");
        assertThat(jsonContent).extractingJsonPathStringValue("$.booker.email").isEqualTo("booker@example.com");
        assertThat(jsonContent).extractingJsonPathNumberValue("$.item.id").isEqualTo(1);
        assertThat(jsonContent).extractingJsonPathStringValue("$.item.name").isEqualTo("Item Name");
        assertThat(jsonContent).extractingJsonPathStringValue("$.item.description").isEqualTo("Item Description");
        assertThat(jsonContent).extractingJsonPathBooleanValue("$.item.available").isEqualTo(true);
    }

    @Test
    void testDeserialize() throws Exception {
        String jsonContent = "{"
                + "  \"id\": 1,"
                + "  \"start\": \"2023-10-01T10:00:00\","
                + "  \"end\": \"2023-10-01T12:00:00\","
                + "  \"status\": \"WAITING\","
                + "  \"booker\": {"
                + "    \"id\": 1,"
                + "    \"name\": \"Booker Name\","
                + "    \"email\": \"booker@example.com\""
                + "  },"
                + "  \"item\": {"
                + "    \"id\": 1,"
                + "    \"name\": \"Item Name\","
                + "    \"description\": \"Item Description\","
                + "    \"available\": true,"
                + "    \"requestId\": null"
                + "  }"
                + "}";

        UserDto bookerDto = new UserDto(1L, "Booker Name", "booker@example.com");
        ItemDto itemDto = new ItemDto(null, "Item Name", "Item Description", true, null);

        LocalDateTime start = LocalDateTime.parse("2023-10-01T10:00:00");
        LocalDateTime end = LocalDateTime.parse("2023-10-01T12:00:00");

        BookingDto expectedBookingDto = new BookingDto(1L, start, end, BookingStatus.WAITING, bookerDto, itemDto);

        assertThat(json.parse(jsonContent)).usingRecursiveComparison().isEqualTo(expectedBookingDto);
    }

}