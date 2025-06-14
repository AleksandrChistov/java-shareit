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
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository repository;
    private final ItemRepository itemRepository;
    private final UserService userService;

    @Override
    public BookingDto create(BookingDto bookingDto) {
        UserDto bookerDto = userService.getById(bookingDto.getBookerId());
        User booker = UserMapper.toUser(bookerDto);

        Item item = itemRepository.getById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещь с id = " + bookingDto.getItemId() + " не найдена"));

        Booking booking = BookingMapper.toBooking(bookingDto, item, booker);

        Booking created = repository.create(booking);

        return BookingMapper.toBookingDto(created);
    }

    @Override
    public BookingDto update(UpdateBookingDto bookingDto) {
        Booking oldBooking = repository.getById(bookingDto.getId())
                .orElseThrow(() -> new NotFoundException("Бронирование с id = " + bookingDto.getId() + " не найдено"));

        Booking updated = repository.update(BookingUtils.updateBooking(oldBooking, bookingDto));

        return BookingMapper.toBookingDto(updated);
    }
}
