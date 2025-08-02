package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

import static ru.practicum.shareit.share.constant.HttpHeadersConstants.X_SHARER_USER_ID;

@RestController
@RequestMapping(path = "/requests", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @GetMapping
    public List<ItemRequestWithItemsDto> getAllOfOwnerWithItems(
            @RequestHeader(value = X_SHARER_USER_ID) Long userId
    ) {
        return itemRequestService.getAllOfOwnerWithItems(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAll() {
        return itemRequestService.getAll();
    }

    @GetMapping("/{requestId}")
    public ItemRequestWithItemsDto getById(
            @RequestHeader(value = X_SHARER_USER_ID) Long userId,
            @PathVariable Long requestId
    ) {
        return itemRequestService.getById(userId, requestId);
    }

    @PostMapping
    public ItemRequestDto create(
            @RequestHeader(value = X_SHARER_USER_ID) Long userId,
            @RequestBody CreateItemRequestDto itemRequestDto
    ) {
        return itemRequestService.create(itemRequestDto, userId);
    }

}
