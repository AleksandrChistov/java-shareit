
package ru.practicum.shareit.item.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreateCommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.Instant;

@UtilityClass
public class CommentMapper {

    public CommentDto toCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreated()
        );
    }

    public Comment toComment(CreateCommentDto createCommentDto, Item item, User booker) {
        Comment comment = new Comment();
        comment.setText(createCommentDto.getText());
        comment.setItem(item);
        comment.setAuthor(booker);
        comment.setCreated(Instant.now());
        return comment;
    }

}
