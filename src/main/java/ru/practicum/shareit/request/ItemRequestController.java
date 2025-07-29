package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

import static ru.practicum.shareit.share.constant.HttpHeadersConstants.X_SHARER_USER_ID;
import static ru.practicum.shareit.user.utils.UserMessageUtils.NOT_NULL_USER_ID_MESSAGE;
import static ru.practicum.shareit.user.utils.UserMessageUtils.POSITIVE_USER_ID_MESSAGE;

@RestController
@RequestMapping(path = "/requests", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @GetMapping
    public List<ItemRequestWithItemsDto> getAllOfOwnerWithItems(
            @RequestHeader(value = X_SHARER_USER_ID) @NotNull(message = NOT_NULL_USER_ID_MESSAGE)
            @Positive(message = POSITIVE_USER_ID_MESSAGE) Long userId
    ) {
        return itemRequestService.getAllOfOwnerWithItems(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAll() {
        return itemRequestService.getAll();
    }

    @GetMapping("/{requestId}")
    public ItemRequestWithItemsDto getById(
            @PathVariable @NotNull(message = "ID запроса вещи не может быть null")
            @Positive(message = "ID запроса вещи не может быть меньше 1") Long requestId
    ) {
        return itemRequestService.getById(requestId);
    }

    @PostMapping
    public ItemRequestDto create(
            @RequestHeader(value = X_SHARER_USER_ID) @NotNull(message = NOT_NULL_USER_ID_MESSAGE)
            @Positive(message = POSITIVE_USER_ID_MESSAGE) Long userId,
            @Valid @RequestBody CreateItemRequestDto itemRequestDto
    ) {
        return itemRequestService.create(itemRequestDto, userId);
    }

}
