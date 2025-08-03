package ru.practicum.shareit.booking.service;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.enums.BookingStatusView;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Transactional
@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class BookingServiceImplIntegrationTest {

    private final EntityManager em;
    private final BookingService bookingService;

    private Long bookerId;

    @BeforeEach
    void setUp() {
        User booker = new User();
        booker.setName("Booker");
        booker.setEmail("booker@example.com");
        em.persist(booker);
        bookerId = booker.getId();

        User owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@example.com");
        em.persist(owner);

        Item item = new Item();
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setOwner(owner);
        em.persist(item);

        Instant nowUtc = Instant.now();

        Booking pastBooking = new Booking();
        pastBooking.setStart(nowUtc.minusSeconds(86400)); // 1 день назад
        pastBooking.setEnd(nowUtc.minusSeconds(3600));    // 1 час назад
        pastBooking.setItem(item);
        pastBooking.setBooker(booker);
        pastBooking.setStatus(BookingStatus.APPROVED);
        em.persist(pastBooking);

        Booking currentBooking = new Booking();
        currentBooking.setStart(nowUtc.minusSeconds(1800)); // 30 минут назад
        currentBooking.setEnd(nowUtc.plusSeconds(1800));    // через 30 минут
        currentBooking.setItem(item);
        currentBooking.setBooker(booker);
        currentBooking.setStatus(BookingStatus.APPROVED);
        em.persist(currentBooking);

        Booking futureBooking = new Booking();
        futureBooking.setStart(nowUtc.plusSeconds(3600));  // через 1 час
        futureBooking.setEnd(nowUtc.plusSeconds(7200));     // через 2 часа
        futureBooking.setItem(item);
        futureBooking.setBooker(booker);
        futureBooking.setStatus(BookingStatus.APPROVED);
        em.persist(futureBooking);

        Booking waitingBooking = new Booking();
        waitingBooking.setStart(nowUtc.plusSeconds(10800)); // через 3 часа
        waitingBooking.setEnd(nowUtc.plusSeconds(14400));   // через 4 часа
        waitingBooking.setItem(item);
        waitingBooking.setBooker(booker);
        waitingBooking.setStatus(BookingStatus.WAITING);
        em.persist(waitingBooking);

        Booking rejectedBooking = new Booking();
        rejectedBooking.setStart(nowUtc.plusSeconds(18000)); // через 5 часов
        rejectedBooking.setEnd(nowUtc.plusSeconds(21600));    // через 6 часов
        rejectedBooking.setItem(item);
        rejectedBooking.setBooker(booker);
        rejectedBooking.setStatus(BookingStatus.REJECTED);
        em.persist(rejectedBooking);

        em.flush(); // Фиксируем изменения в БД
    }

    @Test
    void getAllByBooker_shouldReturnCurrentBookings() {
        List<BookingDto> result = bookingService.getAllByBooker(bookerId, BookingStatusView.CURRENT);
        assertEquals(1, result.size());
        assertTrue(result.getFirst().getStart().isBefore(LocalDateTime.now()));
        assertTrue(result.getFirst().getEnd().isAfter(LocalDateTime.now()));
    }

    @Test
    void getAllByBooker_shouldReturnPastBookings() {
        List<BookingDto> result = bookingService.getAllByBooker(bookerId, BookingStatusView.PAST);
        assertEquals(1, result.size());
        assertTrue(result.getFirst().getEnd().isBefore(LocalDateTime.now()));
    }

    @Test
    void getAllByBooker_shouldReturnFutureBookings() {
        List<BookingDto> result = bookingService.getAllByBooker(bookerId, BookingStatusView.FUTURE);
        assertEquals(3, result.size());
        assertTrue(result.getFirst().getStart().isAfter(LocalDateTime.now()));
    }

    @Test
    void getAllByBooker_shouldReturnWaitingBookings() {
        List<BookingDto> result = bookingService.getAllByBooker(bookerId, BookingStatusView.WAITING);
        assertEquals(1, result.size());
        assertEquals(BookingStatus.WAITING, result.getFirst().getStatus());
    }

    @Test
    void getAllByBooker_shouldReturnRejectedBookings() {
        List<BookingDto> result = bookingService.getAllByBooker(bookerId, BookingStatusView.REJECTED);
        assertEquals(1, result.size());
        assertEquals(BookingStatus.REJECTED, result.getFirst().getStatus());
    }

    @Test
    void getAllByBooker_shouldReturnAllBookings() {
        List<BookingDto> result = bookingService.getAllByBooker(bookerId, BookingStatusView.ALL);
        assertEquals(5, result.size());
    }

}