package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.enums.BookingStatusView;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.core.error.exception.LackOfRightsException;
import ru.practicum.shareit.core.error.exception.NotAvailableException;
import ru.practicum.shareit.core.error.exception.NotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.share.util.DateTimeUtils;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository repository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public BookingDto create(CreateBookingDto bookingDto, Long bookerId) {
        User booker = userRepository.findById(bookerId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + bookerId + " не найден"));

        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещь с id = " + bookingDto.getItemId() + " не найдена"));

        if (!item.getAvailable()) {
            throw new NotAvailableException("Вещь с id = " + bookingDto.getItemId() + " не доступна для бронирования");
        }

        Booking booking = BookingMapper.toBooking(bookingDto, item, booker);

        Booking created = repository.save(booking);

        return BookingMapper.toBookingDto(created);
    }

    @Override
    public BookingDto approve(Long userId, Long bookingId, Boolean approved) {
        Booking booking = findByIdOrThrow(bookingId);
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new LackOfRightsException(
                    String.format("Пользователь с ID %d не является владельцем вещи с ID %d", userId, booking.getItem().getId())
            );
        }
        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        Booking saved = repository.save(booking);
        return BookingMapper.toBookingDto(saved);
    }

    @Override
    public BookingDto getById(Long userId, Long bookingId) {
        Booking booking = findByIdOrThrow(bookingId);
        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new LackOfRightsException(
                    String.format(
                            "Пользователь с ID %d не является автором бронирования или владельцем вещи с ID %d",
                            userId, booking.getItem().getId()
                    )
            );
        }
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getAllByBooker(Long userId, BookingStatusView state) {
        Instant nowUtc = DateTimeUtils.toUTC(LocalDateTime.now());
        List<Booking> bookings = switch (state) {
            case CURRENT -> repository.findAllByBooker_IdAndEndIsAfterAndStartIsBeforeOrderByStartDesc(
                    userId, nowUtc, nowUtc);
            case PAST -> repository.findAllByBooker_IdAndEndIsBeforeOrderByStartDesc(userId, nowUtc);
            case FUTURE -> repository.findAllByBooker_IdAndStartIsAfterOrderByStartDesc(userId, nowUtc);
            case WAITING -> repository.findAllByBooker_IdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
            case REJECTED -> repository.findAllByBooker_IdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
            case ALL -> repository.findAllByBooker_IdOrderByStartDesc(userId);
        };
        return bookings.stream().map(BookingMapper::toBookingDto).toList();
    }

    @Override
    public List<BookingDto> getAllByOwner(Long userId, BookingStatusView state) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));

        Instant nowUtc = DateTimeUtils.toUTC(LocalDateTime.now());

        List<Booking> bookings = switch (state) {
            case CURRENT -> repository.findAllByItem_Owner_IdAndEndIsAfterAndStartIsBeforeOrderByStartDesc(
                    userId, nowUtc, nowUtc);
            case PAST -> repository.findAllByItem_Owner_IdAndEndIsBeforeOrderByStartDesc(userId, nowUtc);
            case FUTURE -> repository.findAllByItem_Owner_IdAndStartIsAfterOrderByStartDesc(userId, nowUtc);
            case WAITING -> repository.findAllByItem_Owner_IdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
            case REJECTED -> repository.findAllByItem_Owner_IdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
            case ALL -> repository.findAllByItem_Owner_IdOrderByStartDesc(userId);
        };
        return bookings.stream().map(BookingMapper::toBookingDto).toList();
    }

    private Booking findByIdOrThrow(Long bookingId) {
        return repository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с id = " + bookingId + " не найдено"));
    }
}
