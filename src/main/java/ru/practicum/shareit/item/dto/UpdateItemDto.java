package ru.practicum.shareit.item.dto;

import lombok.Data;

@Data
public class UpdateItemDto {
    private Long id;
    private final String name;
    private final String description;
    private final Boolean available;
    private final Long requestId;
}
