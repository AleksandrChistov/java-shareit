package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static ru.practicum.shareit.share.constant.HttpHeadersConstants.X_SHARER_USER_ID;

@RestController
@RequestMapping(value = "/items", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<FullItemDto> getAllByOwner(@RequestHeader(value = X_SHARER_USER_ID) Long userId) {
        return itemService.getAllByOwner(userId);
    }

    @GetMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public FullItemDto getById(@PathVariable Long itemId) {
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
            @RequestHeader(value = X_SHARER_USER_ID) Long userId,
            @RequestBody ItemDto itemDto
    ) {
        return itemService.create(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto update(
            @RequestHeader(value = X_SHARER_USER_ID) Long userId,
            @PathVariable Long itemId,
            @RequestBody UpdateItemDto itemDto
    ) {
        return itemService.update(userId, itemId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto createComment(
            @RequestHeader(value = X_SHARER_USER_ID) Long userId,
            @PathVariable Long itemId,
            @RequestBody CreateCommentDto commentDto
    ) {
        return itemService.addComment(userId, itemId, commentDto);
    }

}
