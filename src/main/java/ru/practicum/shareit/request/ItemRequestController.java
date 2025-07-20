package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/requests", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @GetMapping
    public List<ItemRequestDto> getAll() {
        return itemRequestService.getAll();
    }

    @GetMapping("/{itemRequestId}")
    public ItemRequestDto getById(
            @PathVariable @NotNull(message = "ID запроса вещи не может быть null")
            @Positive(message = "ID запроса вещи не может быть меньше 1") Long itemRequestId
    ) {
        return itemRequestService.getById(itemRequestId);
    }

    @PostMapping
    public ItemRequestDto create(@Valid @RequestBody CreateItemRequestDto itemRequestDto) {
        return itemRequestService.create(itemRequestDto);
    }

}
