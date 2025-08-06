package ru.practicum.shareit.item.mapper;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreateCommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.share.util.DateTimeUtils;
import ru.practicum.shareit.user.model.User;

import java.time.Instant;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;

class CommentMapperTest {

    @Test
    void toCommentDto_shouldConvertCommentToCommentDto() {
        Instant createdOn = Instant.now();

        User author = new User();
        author.setId(1L);
        author.setName("Author Name");
        author.setEmail("author@example.com");

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("Test comment");
        comment.setAuthor(author);
        comment.setCreatedOn(createdOn);

        CommentDto result = CommentMapper.toCommentDto(comment);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test comment", result.getText());
        assertEquals("Author Name", result.getAuthorName());
        assertEquals(createdOn, result.getCreated());
    }

    @Test
    void toCommentDto_shouldConvertCommentWithNullFieldsToCommentDto() {
        User author = new User();
        author.setName(null);

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText(null);
        comment.setAuthor(author);
        comment.setCreatedOn(null);

        CommentDto result = CommentMapper.toCommentDto(comment);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertNull(result.getText());
        assertNull(result.getAuthorName());
        assertNull(result.getCreated());
    }

    @Test
    void toComment_shouldConvertCreateCommentDtoAndItemAndUserToComment() {
        LocalDateTime now = LocalDateTime.now();
        Instant utcNow = now.atZone(java.time.ZoneId.systemDefault()).toInstant();

        CreateCommentDto createCommentDto = new CreateCommentDto("Test comment");

        Item item = new Item();
        item.setId(1L);
        item.setName("Test Item");

        User booker = new User();
        booker.setId(1L);
        booker.setName("Booker Name");
        booker.setEmail("booker@example.com");

        try (MockedStatic<DateTimeUtils> mockedDateTimeUtils = mockStatic(DateTimeUtils.class)) {
            mockedDateTimeUtils.when(() -> DateTimeUtils.toUTC(any())).thenReturn(utcNow);

            Comment result = CommentMapper.toComment(createCommentDto, item, booker);

            assertNotNull(result);
            assertNull(result.getId()); // ID не устанавливается
            assertEquals("Test comment", result.getText());
            assertEquals(item, result.getItem());
            assertEquals(booker, result.getAuthor());
            assertEquals(utcNow, result.getCreatedOn());
        }
    }

}