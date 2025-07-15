package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.view.BookingDatesView;
import ru.practicum.shareit.core.error.exception.LackOfRightsException;
import ru.practicum.shareit.core.error.exception.NotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithDatesDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.utils.ItemUtils;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.utils.UserUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final BookingRepository bookingRepository;

    @Override
    public List<ItemWithDatesDto> getAllByOwner(Long ownerId) {
        List<BookingDatesView> bookingDatesViews = bookingRepository.findLastAndNextBookingDatesByOwnerId(ownerId);

        Map<Long, BookingDatesView> bookingDateMap = bookingDatesViews.stream()
                .collect(Collectors.toMap(BookingDatesView::getItemId, Function.identity()));

        return itemRepository.findAllByOwner_Id(ownerId).stream().map(item -> {
            BookingDatesView bookingDates = bookingDateMap.get(item.getId());

            Instant lastBookingDate = (bookingDates != null) ? bookingDates.getLastBookingDate() : null;
            Instant nextBookingDate = (bookingDates != null) ? bookingDates.getNextBookingDate() : null;

            return ItemMapper.toItemWithDatesDto(item, lastBookingDate, nextBookingDate);
        }).collect(Collectors.toList());
    }

    @Override
    public ItemDto getById(Long itemId) {
        return itemRepository.findById(itemId)
                .map(ItemMapper::toItemDto)
                .orElseThrow(() -> new NotFoundException("Вещь с id = " + itemId + " не найдена"));
    }

    @Override
    public List<ItemDto> searchAll(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }

        return itemRepository.searchAllByText(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto create(Long ownerId, ItemDto itemDto) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException(UserUtils.getUserNotFountMessage(ownerId)));

        ItemRequest itemRequest = null;
        if (itemDto.getRequestId() != null) {
            itemRequest = itemRequestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new NotFoundException("Запрос с id = " + itemDto.getRequestId() + " для вещи не найден"));
        }

        Item item = ItemMapper.toItem(itemDto, owner, itemRequest);

        Item created = itemRepository.save(item);

        return ItemMapper.toItemDto(created);
    }

    @Override
    public ItemDto update(Long ownerId, Long itemId, UpdateItemDto itemDto) {
        Item oldItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id = " + itemDto.getId() + " не найдена"));

        if (!ownerId.equals(oldItem.getOwner().getId())) {
            throw new LackOfRightsException("Редактировать вещь может только её владелец");
        }

        Item updated = itemRepository.save(ItemUtils.updateItem(oldItem, itemDto));

        return ItemMapper.toItemDto(updated);
    }
}
