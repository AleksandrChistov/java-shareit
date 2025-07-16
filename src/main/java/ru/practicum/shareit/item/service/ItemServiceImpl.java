package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.view.BookingDatesView;
import ru.practicum.shareit.core.error.exception.LackOfRightsException;
import ru.practicum.shareit.core.error.exception.NotFoundException;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.utils.ItemUtils;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.utils.UserUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
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
    private final CommentRepository commentRepository;

    @Override
    public List<FullItemDto> getAllByOwner(Long ownerId) {
        List<BookingDatesView> bookingDatesViews = bookingRepository.findLastAndNextBookingDatesByOwnerId(ownerId);
        Map<Long, BookingDatesView> bookingDateMap = bookingDatesViews.stream()
                .collect(Collectors.toMap(BookingDatesView::getItemId, Function.identity()));

        List<Item> ownerItems = itemRepository.findAllByOwner_Id(ownerId);

        Map<Long, List<Comment>> commentsMap = findCommentsByItems(ownerItems);

        return ownerItems.stream().map(item -> {
            BookingDatesView bookingDatesView = bookingDateMap.get(item.getId());
            List<Comment> itemComments = commentsMap.getOrDefault(item.getId(), Collections.emptyList());

            Instant lastBookingDate = (bookingDatesView != null) ? bookingDatesView.getLastBookingDate() : null;
            Instant nextBookingDate = (bookingDatesView != null) ? bookingDatesView.getNextBookingDate() : null;

            return ItemMapper.toFullItemDto(item, lastBookingDate, nextBookingDate, itemComments);
        }).collect(Collectors.toList());
    }

    @Override
    public FullItemDto getById(Long itemId) {
        return itemRepository.findById(itemId)
                .map(item -> {
                    List<Comment> comments = commentRepository.findAllByItem_IdIn(Collections.singletonList(itemId));
                    return ItemMapper.toFullItemDto(item, null, null, comments);
                })
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

    @Override
    public CommentDto addComment(Long userId, Long itemId, CreateCommentDto commentDto) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id = " + itemId + " не найдена"));

        Booking booking = bookingRepository.findAllByItem_IdAndBooker_IdAndEndIsBefore(itemId, userId, Instant.now())
                .stream()
                .findFirst()
                .orElseThrow(() -> new LackOfRightsException("Отзыв может оставить только пользователь, " +
                        "который брал эту вещь в аренду, и только после окончания срока аренды"));

        Comment saved = commentRepository.save(CommentMapper.toComment(commentDto, item, booking.getBooker()));
        return CommentMapper.toCommentDto(saved);
    }

    private Map<Long, List<Comment>> findCommentsByItems(List<Item> items) {
        List<Long> itemIds = items.stream().map(Item::getId).toList();

        List<Comment> comments = itemIds.isEmpty()
                ? Collections.emptyList()
                : commentRepository.findAllByItem_IdIn(itemIds);

        return comments.stream()
                .collect(Collectors.groupingBy(c -> c.getItem().getId()));
    }
}
