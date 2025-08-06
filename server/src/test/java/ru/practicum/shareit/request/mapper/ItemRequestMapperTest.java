package ru.practicum.shareit.request.mapper;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.share.util.DateTimeUtils;
import ru.practicum.shareit.user.model.User;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;

class ItemRequestMapperTest {

    @Test
    void toItemRequestDto_shouldConvertItemRequestToItemRequestDto() {
        LocalDateTime now = LocalDateTime.now();
        Instant instant = now.atZone(java.time.ZoneId.systemDefault()).toInstant();

        User requestor = new User();
        requestor.setId(1L);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("Test description");
        itemRequest.setRequestor(requestor);

        try (MockedStatic<DateTimeUtils> mockedDateTimeUtils = mockStatic(DateTimeUtils.class)) {
            mockedDateTimeUtils.when(() -> DateTimeUtils.toLocalDateTime(itemRequest.getCreated())).thenReturn(now);

            ItemRequestDto result = ItemRequestMapper.toItemRequestDto(itemRequest);

            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertEquals("Test description", result.getDescription());
            assertEquals(now, result.getCreated());
            assertEquals(1L, result.getRequestorId());
        }
    }

    @Test
    void toItemRequestDto_shouldConvertItemRequestWithNullRequestorToItemRequestDto() {
        LocalDateTime now = LocalDateTime.now();

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("Test description");
        itemRequest.setRequestor(null);

        try (MockedStatic<DateTimeUtils> mockedDateTimeUtils = mockStatic(DateTimeUtils.class)) {
            mockedDateTimeUtils.when(() -> DateTimeUtils.toLocalDateTime(itemRequest.getCreated())).thenReturn(now);

            ItemRequestDto result = ItemRequestMapper.toItemRequestDto(itemRequest);

            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertEquals("Test description", result.getDescription());
            assertEquals(now, result.getCreated());
            assertNull(result.getRequestorId());
        }
    }

    @Test
    void toItemRequestWithItemsDto_shouldConvertItemRequestAndItemsToItemRequestWithItemsDto() {
        LocalDateTime now = LocalDateTime.now();

        User requestor = new User();
        requestor.setId(1L);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("Test description");
        itemRequest.setRequestor(requestor);

        ItemDto itemDto = new ItemDto(1L, "Item name", "Item description", true, null);
        List<ItemDto> items = List.of(itemDto);

        try (MockedStatic<DateTimeUtils> mockedDateTimeUtils = mockStatic(DateTimeUtils.class)) {
            mockedDateTimeUtils.when(() -> DateTimeUtils.toLocalDateTime(itemRequest.getCreated())).thenReturn(now);

            ItemRequestWithItemsDto result = ItemRequestMapper.toItemRequestWithItemsDto(itemRequest, items);

            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertEquals("Test description", result.getDescription());
            assertEquals(now, result.getCreated());
            assertEquals(1L, result.getRequestorId());
            assertNotNull(result.getItems());
            assertEquals(1, result.getItems().size());
            assertEquals(itemDto, result.getItems().getFirst());
        }
    }

    @Test
    void toItemRequestWithItemsDto_shouldConvertItemRequestWithEmptyItemsToItemRequestWithItemsDto() {
        LocalDateTime now = LocalDateTime.now();

        User requestor = new User();
        requestor.setId(1L);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("Test description");
        itemRequest.setRequestor(requestor);

        List<ItemDto> items = List.of();

        try (MockedStatic<DateTimeUtils> mockedDateTimeUtils = mockStatic(DateTimeUtils.class)) {
            mockedDateTimeUtils.when(() -> DateTimeUtils.toLocalDateTime(itemRequest.getCreated())).thenReturn(now);

            ItemRequestWithItemsDto result = ItemRequestMapper.toItemRequestWithItemsDto(itemRequest, items);

            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertEquals("Test description", result.getDescription());
            assertEquals(now, result.getCreated());
            assertEquals(1L, result.getRequestorId());
            assertNotNull(result.getItems());
            assertTrue(result.getItems().isEmpty());
        }
    }

    @Test
    void toItemRequest_shouldConvertCreateItemRequestDtoAndUserToItemRequest() {
        CreateItemRequestDto createItemRequestDto = new CreateItemRequestDto("Test description");
        User requestor = new User();
        requestor.setId(1L);
        requestor.setName("Test User");
        requestor.setEmail("test@example.com");

        ItemRequest result = ItemRequestMapper.toItemRequest(createItemRequestDto, requestor);

        assertNotNull(result);
        assertNull(result.getId()); // ID не устанавливается
        assertEquals("Test description", result.getDescription());
        assertEquals(requestor, result.getRequestor());
        assertNotNull(result.getCreated()); // Создается автоматически
    }

}