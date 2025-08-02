package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;

import static ru.practicum.shareit.share.constant.HttpHeadersConstants.X_SHARER_USER_ID;
import static ru.practicum.shareit.user.utils.UserMessageUtils.NOT_NULL_USER_ID_MESSAGE;
import static ru.practicum.shareit.user.utils.UserMessageUtils.POSITIVE_USER_ID_MESSAGE;

@RestController
@RequestMapping(path = "/requests", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @GetMapping
    public ResponseEntity<Object> getAllOfOwnerWithItems(
            @RequestHeader(value = X_SHARER_USER_ID) @NotNull(message = NOT_NULL_USER_ID_MESSAGE)
            @Positive(message = POSITIVE_USER_ID_MESSAGE) Long userId
    ) {
        log.info("Get item requests by owner {}", userId);
        return itemRequestClient.getAllByOwner(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAll() {
        log.info("Get all item requests");
        return itemRequestClient.getItemRequests();
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getById(
            @RequestHeader(value = X_SHARER_USER_ID) @NotNull(message = NOT_NULL_USER_ID_MESSAGE)
            @Positive(message = POSITIVE_USER_ID_MESSAGE) Long userId,
            @PathVariable @NotNull(message = "ID запроса вещи не может быть null")
            @Positive(message = "ID запроса вещи не может быть меньше 1") Long requestId
    ) {
        log.info("Get item request by requestId = {} and ownerId = {}", requestId, userId);
        return itemRequestClient.getItemRequest(requestId, userId);
    }

    @PostMapping
    public ResponseEntity<Object> create(
            @RequestHeader(value = X_SHARER_USER_ID) @NotNull(message = NOT_NULL_USER_ID_MESSAGE)
            @Positive(message = POSITIVE_USER_ID_MESSAGE) Long userId,
            @Valid @RequestBody CreateItemRequestDto itemRequestDto
    ) {
        log.info("Create item request {} by userId = {}", itemRequestDto, userId);
        return itemRequestClient.createItemRequest(userId, itemRequestDto);
    }

}
