package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static ru.practicum.shareit.item.utils.ItemMessageUtils.NOT_NULL_ITEM_ID_MESSAGE;
import static ru.practicum.shareit.item.utils.ItemMessageUtils.POSITIVE_ITEM_ID_MESSAGE;
import static ru.practicum.shareit.share.constant.HttpHeadersConstants.X_SHARER_USER_ID;
import static ru.practicum.shareit.user.utils.UserMessageUtils.NOT_NULL_USER_ID_MESSAGE;
import static ru.practicum.shareit.user.utils.UserMessageUtils.POSITIVE_USER_ID_MESSAGE;

@RestController
@RequestMapping(value = "/items", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<FullItemDto> getAllByOwner(
            @RequestHeader(value = X_SHARER_USER_ID) @NotNull(message = NOT_NULL_USER_ID_MESSAGE)
            @Positive(message = POSITIVE_USER_ID_MESSAGE) Long userId
    ) {
        return itemService.getAllByOwner(userId);
    }

    @GetMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public FullItemDto getById(
            @PathVariable @NotNull(message = NOT_NULL_ITEM_ID_MESSAGE)
            @Positive(message = POSITIVE_ITEM_ID_MESSAGE) Long itemId
    ) {
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
            @RequestHeader(value = X_SHARER_USER_ID) @NotNull(message = NOT_NULL_USER_ID_MESSAGE)
            @Positive(message = POSITIVE_USER_ID_MESSAGE) Long userId,
            @Valid @RequestBody ItemDto itemDto
    ) {
        return itemService.create(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto update(
            @RequestHeader(value = X_SHARER_USER_ID) @NotNull(message = NOT_NULL_USER_ID_MESSAGE)
            @Positive(message = POSITIVE_USER_ID_MESSAGE) Long userId,
            @PathVariable @NotNull(message = NOT_NULL_ITEM_ID_MESSAGE)
            @Positive(message = POSITIVE_ITEM_ID_MESSAGE) Long itemId,
            @Valid @RequestBody UpdateItemDto itemDto
    ) {
        return itemService.update(userId, itemId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto createComment(
            @RequestHeader(value = X_SHARER_USER_ID) @NotNull(message = NOT_NULL_USER_ID_MESSAGE)
            @Positive(message = POSITIVE_USER_ID_MESSAGE) Long userId,
            @PathVariable @NotNull(message = NOT_NULL_ITEM_ID_MESSAGE)
            @Positive(message = POSITIVE_ITEM_ID_MESSAGE) Long itemId,
            @Valid @RequestBody CreateCommentDto commentDto
    ) {
        return itemService.addComment(userId, itemId, commentDto);
    }

}
