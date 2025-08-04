package ru.practicum.shareit.item.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class CommentDto {
    private final Long id;
    private final String text;
    private final String authorName;
    private final Instant created;
}
