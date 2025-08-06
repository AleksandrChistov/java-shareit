package ru.practicum.shareit.booking.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.view.BookingDatesView;

import java.time.Instant;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBooker_IdOrderByStartDesc(Long bookerId);

    List<Booking> findAllByBooker_IdAndEndIsBeforeOrderByStartDesc(Long bookerId, Instant date);

    List<Booking> findAllByBooker_IdAndStartIsAfterOrderByStartDesc(Long bookerId, Instant date);

    List<Booking> findAllByBooker_IdAndEndIsAfterAndStartIsBeforeOrderByStartDesc(Long bookerId, Instant end, Instant start);

    List<Booking> findAllByBooker_IdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status);

    List<Booking> findAllByItem_Owner_IdOrderByStartDesc(Long ownerId);

    List<Booking> findAllByItem_Owner_IdAndEndIsBeforeOrderByStartDesc(Long ownerId, Instant date);

    List<Booking> findAllByItem_Owner_IdAndStartIsAfterOrderByStartDesc(Long ownerId, Instant date);

    List<Booking> findAllByItem_Owner_IdAndEndIsAfterAndStartIsBeforeOrderByStartDesc(Long ownerId, Instant end, Instant start);

    List<Booking> findAllByItem_Owner_IdAndStatusOrderByStartDesc(Long ownerId, BookingStatus status);

    @Query(value = "SELECT b.item_id as itemId, " +
            "       MAX(CASE WHEN b.end_date < CURRENT_TIMESTAMP THEN b.end_date ELSE null END) as lastBooking, " +
            "       MIN(CASE WHEN b.start_date > CURRENT_TIMESTAMP THEN b.start_date ELSE null END) as nextBooking " +
            "FROM bookings as b " +
            "LEFT JOIN items as i ON i.id = b.item_id " +
            "WHERE i.owner_id = :ownerId " +
            "GROUP BY b.item_id", nativeQuery = true)
    List<BookingDatesView> findLastAndNextBookingDatesByOwnerId(@Param("ownerId") Long ownerId);

    List<Booking> findAllByItem_IdAndBooker_IdAndEndIsBefore(Long itemId, Long bookerId, Instant date);
}
