package ru.practicum.shareit.item.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class FullItemDto {
    private final Long id;
    private final String name;
    private final String description;
    private final Boolean available;
    private final LocalDateTime lastBooking;
    private final LocalDateTime nextBooking;
    private final Long requestId;
    private final List<CommentDto> comments;
}
