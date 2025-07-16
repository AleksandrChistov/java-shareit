package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.enums.BookingStatusView;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.core.error.exception.LackOfRightsException;
import ru.practicum.shareit.core.error.exception.NotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository repository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public BookingDto create(BookingDto bookingDto) {
        User booker = userRepository.findById(bookingDto.getBookerId())
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + bookingDto.getBookerId() + " не найден"));

        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещь с id = " + bookingDto.getItemId() + " не найдена"));

        bookingDto.setStatus(null);
        Booking booking = BookingMapper.toBooking(bookingDto, item, booker);

        Booking created = repository.save(booking);

        return BookingMapper.toBookingDto(created);
    }

    @Override
    public void approve(Long userId, Long bookingId, Boolean approved) {
        Booking booking = findByIdOrThrow(bookingId);
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new LackOfRightsException(
                    String.format("Пользователь с ID %d не является владельцем вещи с ID %d", userId, booking.getItem().getId())
            );
        }
        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        repository.save(booking);
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
        List<Booking> bookings = switch (state) {
            case CURRENT -> repository.findAllByBooker_IdAndEndIsAfterAndStartIsBeforeOrderByStartDesc(
                    userId, Instant.now(), Instant.now());
            case PAST -> repository.findAllByBooker_IdAndEndIsBeforeOrderByStartDesc(userId, Instant.now());
            case FUTURE -> repository.findAllByBooker_IdAndStartIsAfterOrderByStartDesc(userId, Instant.now());
            case WAITING -> repository.findAllByBooker_IdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
            case REJECTED -> repository.findAllByBooker_IdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
            case ALL -> repository.findAllByBooker_IdOrderByStartDesc(userId);
        };
        return bookings.stream().map(BookingMapper::toBookingDto).toList();
    }

    @Override
    public List<BookingDto> getAllByOwner(Long userId, BookingStatusView state) {
        List<Booking> bookings = switch (state) {
            case CURRENT -> repository.findAllByItem_Owner_IdAndEndIsAfterAndStartIsBeforeOrderByStartDesc(
                    userId, Instant.now(), Instant.now());
            case PAST -> repository.findAllByItem_Owner_IdAndEndIsBeforeOrderByStartDesc(userId, Instant.now());
            case FUTURE -> repository.findAllByItem_Owner_IdAndStartIsAfterOrderByStartDesc(userId, Instant.now());
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
