package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemDtoJsonTest {

    @Autowired
    private JacksonTester<ItemDto> json;

    @Test
    void testSerialize() throws Exception {
        ItemDto itemDto = new ItemDto(1L, "Дрель", "Простая дрель", true, 1L);

        JsonContent<ItemDto> jsonContent = json.write(itemDto);

        assertThat(jsonContent).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(jsonContent).extractingJsonPathStringValue("$.name").isEqualTo("Дрель");
        assertThat(jsonContent).extractingJsonPathStringValue("$.description").isEqualTo("Простая дрель");
        assertThat(jsonContent).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(jsonContent).extractingJsonPathNumberValue("$.requestId").isEqualTo(1);
    }

    @Test
    void testSerializeWithNullValues() throws Exception {
        ItemDto itemDto = new ItemDto(null, "Дрель", "Простая дрель", true, null);

        JsonContent<ItemDto> jsonContent = json.write(itemDto);

        assertThat(jsonContent).extractingJsonPathNumberValue("$.id").isNull();
        assertThat(jsonContent).extractingJsonPathStringValue("$.name").isEqualTo("Дрель");
        assertThat(jsonContent).extractingJsonPathStringValue("$.description").isEqualTo("Простая дрель");
        assertThat(jsonContent).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(jsonContent).extractingJsonPathNumberValue("$.requestId").isNull();
    }

    @Test
    void testDeserialize() throws Exception {
        String jsonContent = """
                {
                    "id": 1,
                    "name": "Дрель",
                    "description": "Простая дрель",
                    "available": true,
                    "requestId": 1
                }
                """;

        var parsedResult = json.parse(jsonContent);

        assertThat(parsedResult).isNotNull();
        ItemDto itemDto = parsedResult.getObject();

        assertThat(itemDto.getId()).isNull(); // READ_ONLY поле не десериализуется
        assertThat(itemDto.getName()).isEqualTo("Дрель");
        assertThat(itemDto.getDescription()).isEqualTo("Простая дрель");
        assertThat(itemDto.getAvailable()).isEqualTo(true);
        assertThat(itemDto.getRequestId()).isEqualTo(1L);
    }

    @Test
    void testDeserializeWithNullValues() throws Exception {
        String jsonContent = """
                {
                    "id": null,
                    "name": "Дрель",
                    "description": "Простая дрель",
                    "available": true,
                    "requestId": null
                }
                """;

        var parsedResult = json.parse(jsonContent);

        assertThat(parsedResult).isNotNull();
        ItemDto itemDto = parsedResult.getObject();

        assertThat(itemDto.getId()).isNull();
        assertThat(itemDto.getName()).isEqualTo("Дрель");
        assertThat(itemDto.getDescription()).isEqualTo("Простая дрель");
        assertThat(itemDto.getAvailable()).isEqualTo(true);
        assertThat(itemDto.getRequestId()).isNull();
    }

    @Test
    void testDeserializeWithoutOptionalFields() throws Exception {
        String jsonContent = """
                {
                    "name": "Дрель",
                    "description": "Простая дрель",
                    "available": true
                }
                """;

        var parsedResult = json.parse(jsonContent);

        assertThat(parsedResult).isNotNull();
        ItemDto itemDto = parsedResult.getObject();

        assertThat(itemDto.getId()).isNull();
        assertThat(itemDto.getName()).isEqualTo("Дрель");
        assertThat(itemDto.getDescription()).isEqualTo("Простая дрель");
        assertThat(itemDto.getAvailable()).isEqualTo(true);
        assertThat(itemDto.getRequestId()).isNull();
    }
}