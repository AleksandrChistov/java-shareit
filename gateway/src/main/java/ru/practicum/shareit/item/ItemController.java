package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;

import static ru.practicum.shareit.item.utils.ItemMessageUtils.NOT_NULL_ITEM_ID_MESSAGE;
import static ru.practicum.shareit.item.utils.ItemMessageUtils.POSITIVE_ITEM_ID_MESSAGE;
import static ru.practicum.shareit.share.constant.HttpHeadersConstants.X_SHARER_USER_ID;
import static ru.practicum.shareit.user.utils.UserMessageUtils.NOT_NULL_USER_ID_MESSAGE;
import static ru.practicum.shareit.user.utils.UserMessageUtils.POSITIVE_USER_ID_MESSAGE;

@RestController
@RequestMapping(value = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> getAllByOwner(
            @RequestHeader(value = X_SHARER_USER_ID) @NotNull(message = NOT_NULL_USER_ID_MESSAGE)
            @Positive(message = POSITIVE_USER_ID_MESSAGE) Long userId
    ) {
        log.info("Get items by user {}", userId);
        return itemClient.getItemsByOwner(userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getById(
            @PathVariable @NotNull(message = NOT_NULL_ITEM_ID_MESSAGE)
            @Positive(message = POSITIVE_ITEM_ID_MESSAGE) Long itemId
    ) {
        log.info("Get item {}", itemId);
        return itemClient.getItem(itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchAll(@RequestParam String text) {
        log.info("Get items by text = {}", text);
        return itemClient.searchItems(text);
    }

    @PostMapping
    public ResponseEntity<Object> create(
            @RequestHeader(value = X_SHARER_USER_ID) @NotNull(message = NOT_NULL_USER_ID_MESSAGE)
            @Positive(message = POSITIVE_USER_ID_MESSAGE) Long userId,
            @Valid @RequestBody ItemDto itemDto
    ) {
        log.info("Create item {} by user = {}", itemDto, userId);
        return itemClient.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(
            @RequestHeader(value = X_SHARER_USER_ID) @NotNull(message = NOT_NULL_USER_ID_MESSAGE)
            @Positive(message = POSITIVE_USER_ID_MESSAGE) Long userId,
            @PathVariable @NotNull(message = NOT_NULL_ITEM_ID_MESSAGE)
            @Positive(message = POSITIVE_ITEM_ID_MESSAGE) Long itemId,
            @Valid @RequestBody UpdateItemDto itemDto
    ) {
        log.info("Update item {} with {} by user = {}", itemId, itemDto, userId);
        return itemClient.patchItem(userId, itemId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(
            @RequestHeader(value = X_SHARER_USER_ID) @NotNull(message = NOT_NULL_USER_ID_MESSAGE)
            @Positive(message = POSITIVE_USER_ID_MESSAGE) Long userId,
            @PathVariable @NotNull(message = NOT_NULL_ITEM_ID_MESSAGE)
            @Positive(message = POSITIVE_ITEM_ID_MESSAGE) Long itemId,
            @Valid @RequestBody CreateCommentDto commentDto
    ) {
        log.info("Add comment {} to item {} by user {}", commentDto, itemId, userId);
        return itemClient.createComment(userId, itemId, commentDto);
    }

}
