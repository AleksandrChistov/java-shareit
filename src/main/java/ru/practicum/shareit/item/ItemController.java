package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.service.ItemService;

import static ru.practicum.shareit.share.constant.HttpHeadersConstants.*;

import java.util.List;

@RestController
@RequestMapping(value = "/items", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ItemDto> getAllByOwner(@RequestHeader(X_SHARER_USER_ID) Long userId) {
        return itemService.getAllByOwner(userId);
    }

    @GetMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto getById(@PathVariable Long itemId) {
        return itemService.getById(itemId);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<ItemDto> searchAll(@RequestParam String text) {
        return itemService.searchAll(text);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto create(@RequestHeader(X_SHARER_USER_ID) Long userId, @Valid @RequestBody ItemDto itemDto) {
        return itemService.create(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto update(
            @RequestHeader(X_SHARER_USER_ID) Long userId,
            @PathVariable Long itemId,
            @Valid @RequestBody UpdateItemDto itemDto
    ) {
        return itemService.update(userId, itemId, itemDto);
    }

}
