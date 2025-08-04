package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UpdateItemDto {
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private final Long id;
    private final String name;
    private final String description;
    private final Boolean available;
    private final Long requestId;
}
