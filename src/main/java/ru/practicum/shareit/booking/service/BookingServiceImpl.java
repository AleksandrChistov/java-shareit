package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.UpdateBookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.utils.BookingUtils;
import ru.practicum.shareit.core.error.exception.NotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

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

        Booking booking = BookingMapper.toBooking(bookingDto, item, booker);

        Booking created = repository.save(booking);

        return BookingMapper.toBookingDto(created);
    }

    @Override
    public BookingDto update(UpdateBookingDto bookingDto) {
        Booking oldBooking = repository.findById(bookingDto.getId())
                .orElseThrow(() -> new NotFoundException("Бронирование с id = " + bookingDto.getId() + " не найдено"));

        Booking updated = repository.save(BookingUtils.updateBooking(oldBooking, bookingDto));

        return BookingMapper.toBookingDto(updated);
    }
}
