package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.core.validation.ValidId;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static ru.practicum.shareit.share.constant.HttpHeadersConstants.X_SHARER_USER_ID;
import static ru.practicum.shareit.user.utils.UserUtils.INVALID_USER_ID_MESSAGE;

@RestController
@RequestMapping(value = "/items", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ItemDto> getAllByOwner(@RequestHeader(value = X_SHARER_USER_ID, required = false) @ValidId(message = INVALID_USER_ID_MESSAGE) Long userId) {
        return itemService.getAllByOwner(userId);
    }

    @GetMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto getById(@PathVariable @ValidId Long itemId) {
        return itemService.getById(itemId);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<ItemDto> searchAll(@RequestParam String text) {
        return itemService.searchAll(text);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto create(
            @RequestHeader(value = X_SHARER_USER_ID, required = false) @ValidId(message = INVALID_USER_ID_MESSAGE) Long userId,
            @Valid @RequestBody ItemDto itemDto
    ) {
        return itemService.create(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto update(
            @RequestHeader(value = X_SHARER_USER_ID, required = false) @ValidId(message = INVALID_USER_ID_MESSAGE) Long userId,
            @PathVariable @ValidId Long itemId,
            @Valid @RequestBody UpdateItemDto itemDto
    ) {
        return itemService.update(userId, itemId, itemDto);
    }

}
