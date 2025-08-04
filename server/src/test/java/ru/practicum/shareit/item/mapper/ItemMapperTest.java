package ru.practicum.shareit.item.mapper;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.FullItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.share.util.DateTimeUtils;
import ru.practicum.shareit.user.model.User;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;

class ItemMapperTest {

    @Test
    void toItemDto_shouldConvertItemToItemDto() {
        ItemRequest request = new ItemRequest();
        request.setId(1L);

        Item item = new Item();
        item.setId(1L);
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setRequest(request);

        ItemDto result = ItemMapper.toItemDto(item);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Item", result.getName());
        assertEquals("Test Description", result.getDescription());
        assertTrue(result.getAvailable());
        assertEquals(1L, result.getRequestId());
    }

    @Test
    void toItemDto_shouldConvertItemWithNullRequestToItemDto() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setRequest(null);

        ItemDto result = ItemMapper.toItemDto(item);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Item", result.getName());
        assertEquals("Test Description", result.getDescription());
        assertTrue(result.getAvailable());
        assertNull(result.getRequestId());
    }

    @Test
    void toItemDto_shouldConvertItemWithNullFieldsToItemDto() {
        Item item = new Item();
        item.setId(1L);
        item.setName(null);
        item.setDescription(null);
        item.setAvailable(null);
        item.setRequest(null);

        ItemDto result = ItemMapper.toItemDto(item);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertNull(result.getName());
        assertNull(result.getDescription());
        assertNull(result.getAvailable());
        assertNull(result.getRequestId());
    }

    @Test
    void toFullItemDto_shouldConvertItemToFullItemDto() {
        LocalDateTime now = LocalDateTime.now();
        Instant lastBooking = Instant.now().minusSeconds(3600);
        Instant nextBooking = Instant.now().plusSeconds(3600);

        ItemRequest request = new ItemRequest();
        request.setId(1L);

        Item item = new Item();
        item.setId(1L);
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setRequest(request);

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("Test comment");
        List<Comment> comments = List.of(comment);

        try (MockedStatic<DateTimeUtils> mockedDateTimeUtils = mockStatic(DateTimeUtils.class);
             MockedStatic<CommentMapper> mockedCommentMapper = mockStatic(CommentMapper.class)) {

            mockedDateTimeUtils.when(() -> DateTimeUtils.toLocalDateTime(lastBooking)).thenReturn(now.minusHours(1));
            mockedDateTimeUtils.when(() -> DateTimeUtils.toLocalDateTime(nextBooking)).thenReturn(now.plusHours(1));

            CommentDto commentDto = new CommentDto(1L, "Test comment", "Author", Instant.now());
            mockedCommentMapper.when(() -> CommentMapper.toCommentDto(comment)).thenReturn(commentDto);

            FullItemDto result = ItemMapper.toFullItemDto(item, lastBooking, nextBooking, comments);

            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertEquals("Test Item", result.getName());
            assertEquals("Test Description", result.getDescription());
            assertTrue(result.getAvailable());
            assertEquals(now.minusHours(1), result.getLastBooking());
            assertEquals(now.plusHours(1), result.getNextBooking());
            assertEquals(1L, result.getRequestId());
            assertNotNull(result.getComments());
            assertEquals(1, result.getComments().size());
            assertEquals(commentDto, result.getComments().getFirst());
        }
    }

    @Test
    void toFullItemDto_shouldConvertItemWithNullBookingDatesToFullItemDto() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setRequest(null);

        List<Comment> comments = List.of();

        try (MockedStatic<CommentMapper> mockedCommentMapper = mockStatic(CommentMapper.class)) {
            mockedCommentMapper.when(() -> CommentMapper.toCommentDto(any(Comment.class)))
                    .thenThrow(new IllegalArgumentException("Should not be called for empty list"));

            FullItemDto result = ItemMapper.toFullItemDto(item, null, null, comments);

            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertEquals("Test Item", result.getName());
            assertEquals("Test Description", result.getDescription());
            assertTrue(result.getAvailable());
            assertNull(result.getLastBooking());
            assertNull(result.getNextBooking());
            assertNull(result.getRequestId());
            assertNotNull(result.getComments());
            assertTrue(result.getComments().isEmpty());
        }
    }

    @Test
    void toFullItemDto_shouldConvertItemWithEmptyCommentsToFullItemDto() {
        LocalDateTime now = LocalDateTime.now();
        Instant lastBooking = Instant.now().minusSeconds(3600);
        Instant nextBooking = Instant.now().plusSeconds(3600);

        Item item = new Item();
        item.setId(1L);
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);

        List<Comment> comments = List.of();

        try (MockedStatic<DateTimeUtils> mockedDateTimeUtils = mockStatic(DateTimeUtils.class)) {
            mockedDateTimeUtils.when(() -> DateTimeUtils.toLocalDateTime(lastBooking)).thenReturn(now.minusHours(1));
            mockedDateTimeUtils.when(() -> DateTimeUtils.toLocalDateTime(nextBooking)).thenReturn(now.plusHours(1));

            FullItemDto result = ItemMapper.toFullItemDto(item, lastBooking, nextBooking, comments);

            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertEquals("Test Item", result.getName());
            assertEquals("Test Description", result.getDescription());
            assertTrue(result.getAvailable());
            assertEquals(now.minusHours(1), result.getLastBooking());
            assertEquals(now.plusHours(1), result.getNextBooking());
            assertNull(result.getRequestId());
            assertNotNull(result.getComments());
            assertTrue(result.getComments().isEmpty());
        }
    }

    @Test
    void toItem_shouldConvertItemDtoAndOwnerToItem() {
        ItemDto itemDto = new ItemDto(1L, "Test Item", "Test Description", true, 1L);

        User owner = new User();
        owner.setId(1L);
        owner.setName("Owner");

        ItemRequest request = new ItemRequest();
        request.setId(1L);

        Item result = ItemMapper.toItem(itemDto, owner, request);

        assertNotNull(result);
        assertNull(result.getId()); // ID не устанавливается
        assertEquals("Test Item", result.getName());
        assertEquals("Test Description", result.getDescription());
        assertTrue(result.getAvailable());
        assertEquals(owner, result.getOwner());
        assertEquals(request, result.getRequest());
    }

    @Test
    void toItem_shouldConvertItemDtoWithNullRequestToItem() {
        ItemDto itemDto = new ItemDto(1L, "Test Item", "Test Description", true, null);

        User owner = new User();
        owner.setId(1L);
        owner.setName("Owner");

        Item result = ItemMapper.toItem(itemDto, owner, null);

        assertNotNull(result);
        assertNull(result.getId()); // ID не устанавливается
        assertEquals("Test Item", result.getName());
        assertEquals("Test Description", result.getDescription());
        assertTrue(result.getAvailable());
        assertEquals(owner, result.getOwner());
        assertNull(result.getRequest());
    }

}