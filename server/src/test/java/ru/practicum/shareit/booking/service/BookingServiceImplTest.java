package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository repository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private User booker;
    private User owner;
    private Item item;
    private Booking booking;

    @BeforeEach
    void setUp() {
        booker = new User();
        booker.setId(1L);
        booker.setName("Test Booker");
        booker.setEmail("booker@example.com");

        owner = new User();
        owner.setId(2L);
        owner.setName("Test Owner");
        owner.setEmail("owner@example.com");

        item = new Item();
        item.setId(1L);
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setOwner(owner);

        booking = new Booking();
        booking.setId(1L);
        booking.setStart(Instant.now().plusSeconds(3600));
        booking.setEnd(Instant.now().plusSeconds(7200));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);
    }

    @Test
    void create_shouldReturnBookingDto() {
        CreateBookingDto createBookingDto = new CreateBookingDto(1L, LocalDateTime.now().plusSeconds(3600), LocalDateTime.now().plusSeconds(7200));
        BookingDto bookingDto = new BookingDto(1L, LocalDateTime.now().plusSeconds(3600), LocalDateTime.now().plusSeconds(7200),
                BookingStatus.WAITING, null, null);

        try (MockedStatic<BookingMapper> mockedMapper = mockStatic(BookingMapper.class)) {
            mockedMapper.when(() -> BookingMapper.toBooking(any(CreateBookingDto.class), any(Item.class), any(User.class)))
                    .thenReturn(booking);
            mockedMapper.when(() -> BookingMapper.toBookingDto(any(Booking.class)))
                    .thenReturn(bookingDto);

            when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
            when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
            when(repository.save(any(Booking.class))).thenReturn(booking);

            BookingDto result = bookingService.create(createBookingDto, 1L);

            assertNotNull(result);
            assertEquals(bookingDto, result);
            verify(userRepository).findById(1L);
            verify(itemRepository).findById(1L);
            verify(repository).save(any(Booking.class));
            mockedMapper.verify(() -> BookingMapper.toBooking(any(CreateBookingDto.class), any(Item.class), any(User.class)));
            mockedMapper.verify(() -> BookingMapper.toBookingDto(booking));
        }
    }

    @Test
    void create_shouldThrowNotFoundExceptionWhenBookerNotFound() {
        Long bookerId = 999L;
        CreateBookingDto createBookingDto = new CreateBookingDto(1L, LocalDateTime.now().plusSeconds(3600), LocalDateTime.now().plusSeconds(7200));

        when(userRepository.findById(bookerId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.create(createBookingDto, bookerId));
        verify(userRepository).findById(bookerId);
        verifyNoInteractions(itemRepository);
        verifyNoInteractions(repository);
    }

    @Test
    void create_shouldThrowNotFoundExceptionWhenItemNotFound() {
        Long itemId = 999L;
        CreateBookingDto createBookingDto = new CreateBookingDto(itemId, LocalDateTime.now().plusSeconds(3600), LocalDateTime.now().plusSeconds(7200));

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.create(createBookingDto, 1L));
        verify(userRepository).findById(1L);
        verify(itemRepository).findById(itemId);
        verifyNoInteractions(repository);
    }

    @Test
    void create_shouldThrowNotAvailableExceptionWhenItemNotAvailable() {
        CreateBookingDto createBookingDto = new CreateBookingDto(1L, LocalDateTime.now().plusSeconds(3600), LocalDateTime.now().plusSeconds(7200));
        item.setAvailable(false);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        assertThrows(NotAvailableException.class, () -> bookingService.create(createBookingDto, 1L));
        verify(userRepository).findById(1L);
        verify(itemRepository).findById(1L);
        verifyNoInteractions(repository);
    }

    @Test
    void approve_shouldReturnApprovedBookingDto() {
        Long bookingId = 1L;
        BookingDto bookingDto = new BookingDto(bookingId, LocalDateTime.now().plusSeconds(3600), LocalDateTime.now().plusSeconds(7200),
                BookingStatus.APPROVED, null, null);

        try (MockedStatic<BookingMapper> mockedMapper = mockStatic(BookingMapper.class)) {
            mockedMapper.when(() -> BookingMapper.toBookingDto(any(Booking.class)))
                    .thenReturn(bookingDto);

            when(repository.findById(anyLong())).thenReturn(Optional.of(booking));
            when(repository.save(any(Booking.class))).thenReturn(booking);

            BookingDto result = bookingService.approve(booking.getItem().getOwner().getId(), bookingId, true);

            assertNotNull(result);
            assertEquals(bookingDto, result);
            assertEquals(BookingStatus.APPROVED, booking.getStatus());
            verify(repository).findById(bookingId);
            verify(repository).save(booking);
            mockedMapper.verify(() -> BookingMapper.toBookingDto(booking));
        }
    }

    @Test
    void approve_shouldReturnRejectedBookingDto() {
        Long bookingId = 1L;
        BookingDto bookingDto = new BookingDto(bookingId, LocalDateTime.now().plusSeconds(3600), LocalDateTime.now().plusSeconds(7200),
                BookingStatus.REJECTED, null, null);

        try (MockedStatic<BookingMapper> mockedMapper = mockStatic(BookingMapper.class)) {
            mockedMapper.when(() -> BookingMapper.toBookingDto(any(Booking.class)))
                    .thenReturn(bookingDto);

            when(repository.findById(anyLong())).thenReturn(Optional.of(booking));
            when(repository.save(any(Booking.class))).thenReturn(booking);

            BookingDto result = bookingService.approve(booking.getItem().getOwner().getId(), bookingId, false);

            assertNotNull(result);
            assertEquals(bookingDto, result);
            assertEquals(BookingStatus.REJECTED, booking.getStatus());
            verify(repository).findById(bookingId);
            verify(repository).save(booking);
            mockedMapper.verify(() -> BookingMapper.toBookingDto(booking));
        }
    }

    @Test
    void approve_shouldThrowNotFoundExceptionWhenBookingNotFound() {
        Long bookingId = 999L;

        when(repository.findById(bookingId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.approve(1L, bookingId, true));
        verify(repository).findById(bookingId);
        verify(repository, never()).save(any());
    }

    @Test
    void approve_shouldThrowLackOfRightsExceptionWhenUserIsNotOwner() {
        Long userId = 3L; // Не владелец
        Long bookingId = 1L;

        when(repository.findById(anyLong())).thenReturn(Optional.of(booking));

        assertThrows(LackOfRightsException.class, () -> bookingService.approve(userId, bookingId, true));
        verify(repository).findById(bookingId);
        verify(repository, never()).save(any());
    }

    @Test
    void getById_shouldReturnBookingDtoWhenUserIsBooker() {
        Long bookingId = 1L;
        BookingDto bookingDto = new BookingDto(bookingId, LocalDateTime.now().plusSeconds(3600), LocalDateTime.now().plusSeconds(7200),
                BookingStatus.WAITING, null, null);

        try (MockedStatic<BookingMapper> mockedMapper = mockStatic(BookingMapper.class)) {
            mockedMapper.when(() -> BookingMapper.toBookingDto(any(Booking.class)))
                    .thenReturn(bookingDto);

            when(repository.findById(anyLong())).thenReturn(Optional.of(booking));

            BookingDto result = bookingService.getById(booker.getId(), bookingId);

            assertNotNull(result);
            assertEquals(bookingDto, result);
            verify(repository).findById(bookingId);
            mockedMapper.verify(() -> BookingMapper.toBookingDto(booking));
        }
    }

    @Test
    void getById_shouldReturnBookingDtoWhenUserIsOwner() {
        Long bookingId = 1L;
        BookingDto bookingDto = new BookingDto(bookingId, LocalDateTime.now().plusSeconds(3600), LocalDateTime.now().plusSeconds(7200),
                BookingStatus.WAITING, null, null);

        try (MockedStatic<BookingMapper> mockedMapper = mockStatic(BookingMapper.class)) {
            mockedMapper.when(() -> BookingMapper.toBookingDto(any(Booking.class)))
                    .thenReturn(bookingDto);

            when(repository.findById(anyLong())).thenReturn(Optional.of(booking));

            BookingDto result = bookingService.getById(owner.getId(), bookingId);

            assertNotNull(result);
            assertEquals(bookingDto, result);
            verify(repository).findById(bookingId);
            mockedMapper.verify(() -> BookingMapper.toBookingDto(booking));
        }
    }

    @Test
    void getById_shouldThrowNotFoundExceptionWhenBookingNotFound() {
        Long bookingId = 999L;

        when(repository.findById(bookingId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.getById(booker.getId(), bookingId));
        verify(repository).findById(bookingId);
    }

    @Test
    void getById_shouldThrowLackOfRightsExceptionWhenUserIsNotRelatedToBooking() {
        Long userId = 3L; // Не владелец и не бронирующий
        Long bookingId = 1L;

        when(repository.findById(anyLong())).thenReturn(Optional.of(booking));

        assertThrows(LackOfRightsException.class, () -> bookingService.getById(userId, bookingId));
        verify(repository).findById(bookingId);
    }

    @Test
    void getAllByBooker_shouldReturnCurrentBookings() {
        BookingDto bookingDto = new BookingDto(1L, LocalDateTime.now().plusSeconds(3600), LocalDateTime.now().plusSeconds(7200),
                BookingStatus.WAITING, null, null);
        List<Booking> bookings = List.of(booking);

        try (MockedStatic<BookingMapper> mockedMapper = mockStatic(BookingMapper.class);
             MockedStatic<DateTimeUtils> mockedDateTimeUtils = mockStatic(DateTimeUtils.class)) {

            mockedMapper.when(() -> BookingMapper.toBookingDto(any(Booking.class)))
                    .thenReturn(bookingDto);
            mockedDateTimeUtils.when(() -> DateTimeUtils.toUTC(any(LocalDateTime.class)))
                    .thenReturn(Instant.now());

            when(repository.findAllByBooker_IdAndEndIsAfterAndStartIsBeforeOrderByStartDesc(anyLong(), any(Instant.class), any(Instant.class)))
                    .thenReturn(bookings);

            List<BookingDto> result = bookingService.getAllByBooker(booker.getId(), BookingStatusView.CURRENT);

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(bookingDto, result.getFirst());
            verify(repository).findAllByBooker_IdAndEndIsAfterAndStartIsBeforeOrderByStartDesc(anyLong(), any(Instant.class), any(Instant.class));
            mockedMapper.verify(() -> BookingMapper.toBookingDto(booking));
        }
    }

    @Test
    void getAllByBooker_shouldReturnPastBookings() {
        BookingDto bookingDto = new BookingDto(1L, LocalDateTime.now().plusSeconds(3600), LocalDateTime.now().plusSeconds(7200),
                BookingStatus.WAITING, null, null);
        List<Booking> bookings = List.of(booking);

        try (MockedStatic<BookingMapper> mockedMapper = mockStatic(BookingMapper.class);
             MockedStatic<DateTimeUtils> mockedDateTimeUtils = mockStatic(DateTimeUtils.class)) {

            mockedMapper.when(() -> BookingMapper.toBookingDto(any(Booking.class)))
                    .thenReturn(bookingDto);
            mockedDateTimeUtils.when(() -> DateTimeUtils.toUTC(any(LocalDateTime.class)))
                    .thenReturn(Instant.now());

            when(repository.findAllByBooker_IdAndEndIsBeforeOrderByStartDesc(anyLong(), any(Instant.class)))
                    .thenReturn(bookings);

            List<BookingDto> result = bookingService.getAllByBooker(booker.getId(), BookingStatusView.PAST);

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(bookingDto, result.getFirst());
            verify(repository).findAllByBooker_IdAndEndIsBeforeOrderByStartDesc(anyLong(), any(Instant.class));
            mockedMapper.verify(() -> BookingMapper.toBookingDto(booking));
        }
    }

    @Test
    void getAllByBooker_shouldReturnFutureBookings() {
        BookingDto bookingDto = new BookingDto(1L, LocalDateTime.now().plusSeconds(3600), LocalDateTime.now().plusSeconds(7200),
                BookingStatus.WAITING, null, null);
        List<Booking> bookings = List.of(booking);

        try (MockedStatic<BookingMapper> mockedMapper = mockStatic(BookingMapper.class);
             MockedStatic<DateTimeUtils> mockedDateTimeUtils = mockStatic(DateTimeUtils.class)) {

            mockedMapper.when(() -> BookingMapper.toBookingDto(any(Booking.class)))
                    .thenReturn(bookingDto);
            mockedDateTimeUtils.when(() -> DateTimeUtils.toUTC(any(LocalDateTime.class)))
                    .thenReturn(Instant.now());

            when(repository.findAllByBooker_IdAndStartIsAfterOrderByStartDesc(anyLong(), any(Instant.class)))
                    .thenReturn(bookings);

            List<BookingDto> result = bookingService.getAllByBooker(booker.getId(), BookingStatusView.FUTURE);

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(bookingDto, result.getFirst());
            verify(repository).findAllByBooker_IdAndStartIsAfterOrderByStartDesc(anyLong(), any(Instant.class));
            mockedMapper.verify(() -> BookingMapper.toBookingDto(booking));
        }
    }

    @Test
    void getAllByBooker_shouldReturnWaitingBookings() {
        BookingDto bookingDto = new BookingDto(1L, LocalDateTime.now().plusSeconds(3600), LocalDateTime.now().plusSeconds(7200),
                BookingStatus.WAITING, null, null);
        List<Booking> bookings = List.of(booking);

        try (MockedStatic<BookingMapper> mockedMapper = mockStatic(BookingMapper.class)) {
            mockedMapper.when(() -> BookingMapper.toBookingDto(any(Booking.class)))
                    .thenReturn(bookingDto);

            when(repository.findAllByBooker_IdAndStatusOrderByStartDesc(anyLong(), any(BookingStatus.class)))
                    .thenReturn(bookings);

            List<BookingDto> result = bookingService.getAllByBooker(booker.getId(), BookingStatusView.WAITING);

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(bookingDto, result.getFirst());
            verify(repository).findAllByBooker_IdAndStatusOrderByStartDesc(anyLong(), eq(BookingStatus.WAITING));
            mockedMapper.verify(() -> BookingMapper.toBookingDto(booking));
        }
    }

    @Test
    void getAllByBooker_shouldReturnRejectedBookings() {
        BookingDto bookingDto = new BookingDto(1L, LocalDateTime.now().plusSeconds(3600), LocalDateTime.now().plusSeconds(7200),
                BookingStatus.REJECTED, null, null);
        List<Booking> bookings = List.of(booking);

        try (MockedStatic<BookingMapper> mockedMapper = mockStatic(BookingMapper.class)) {
            mockedMapper.when(() -> BookingMapper.toBookingDto(any(Booking.class)))
                    .thenReturn(bookingDto);

            when(repository.findAllByBooker_IdAndStatusOrderByStartDesc(anyLong(), any(BookingStatus.class)))
                    .thenReturn(bookings);

            List<BookingDto> result = bookingService.getAllByBooker(booker.getId(), BookingStatusView.REJECTED);

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(bookingDto, result.getFirst());
            verify(repository).findAllByBooker_IdAndStatusOrderByStartDesc(anyLong(), eq(BookingStatus.REJECTED));
            mockedMapper.verify(() -> BookingMapper.toBookingDto(booking));
        }
    }

    @Test
    void getAllByBooker_shouldReturnAllBookings() {
        BookingDto bookingDto = new BookingDto(1L, LocalDateTime.now().plusSeconds(3600), LocalDateTime.now().plusSeconds(7200),
                BookingStatus.WAITING, null, null);
        List<Booking> bookings = List.of(booking);

        try (MockedStatic<BookingMapper> mockedMapper = mockStatic(BookingMapper.class)) {
            mockedMapper.when(() -> BookingMapper.toBookingDto(any(Booking.class)))
                    .thenReturn(bookingDto);

            when(repository.findAllByBooker_IdOrderByStartDesc(anyLong()))
                    .thenReturn(bookings);

            List<BookingDto> result = bookingService.getAllByBooker(booker.getId(), BookingStatusView.ALL);

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(bookingDto, result.getFirst());
            verify(repository).findAllByBooker_IdOrderByStartDesc(anyLong());
            mockedMapper.verify(() -> BookingMapper.toBookingDto(booking));
        }
    }

    @Test
    void getAllByOwner_shouldReturnCurrentBookings() {
        BookingDto bookingDto = new BookingDto(1L, LocalDateTime.now().plusSeconds(3600), LocalDateTime.now().plusSeconds(7200),
                BookingStatus.WAITING, null, null);
        List<Booking> bookings = List.of(booking);

        try (MockedStatic<BookingMapper> mockedMapper = mockStatic(BookingMapper.class);
             MockedStatic<DateTimeUtils> mockedDateTimeUtils = mockStatic(DateTimeUtils.class)) {

            mockedMapper.when(() -> BookingMapper.toBookingDto(any(Booking.class)))
                    .thenReturn(bookingDto);
            mockedDateTimeUtils.when(() -> DateTimeUtils.toUTC(any(LocalDateTime.class)))
                    .thenReturn(Instant.now());

            when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
            when(repository.findAllByItem_Owner_IdAndEndIsAfterAndStartIsBeforeOrderByStartDesc(anyLong(), any(Instant.class), any(Instant.class)))
                    .thenReturn(bookings);

            List<BookingDto> result = bookingService.getAllByOwner(owner.getId(), BookingStatusView.CURRENT);

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(bookingDto, result.getFirst());
            verify(userRepository).findById(anyLong());
            verify(repository).findAllByItem_Owner_IdAndEndIsAfterAndStartIsBeforeOrderByStartDesc(anyLong(), any(Instant.class), any(Instant.class));
            mockedMapper.verify(() -> BookingMapper.toBookingDto(booking));
        }
    }

    @Test
    void getAllByOwner_shouldReturnPastBookings() {
        BookingDto bookingDto = new BookingDto(1L, LocalDateTime.now().plusSeconds(3600), LocalDateTime.now().plusSeconds(7200),
                BookingStatus.WAITING, null, null);
        List<Booking> bookings = List.of(booking);

        try (MockedStatic<BookingMapper> mockedMapper = mockStatic(BookingMapper.class);
             MockedStatic<DateTimeUtils> mockedDateTimeUtils = mockStatic(DateTimeUtils.class)) {

            mockedMapper.when(() -> BookingMapper.toBookingDto(any(Booking.class)))
                    .thenReturn(bookingDto);
            mockedDateTimeUtils.when(() -> DateTimeUtils.toUTC(any(LocalDateTime.class)))
                    .thenReturn(Instant.now());

            when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
            when(repository.findAllByItem_Owner_IdAndEndIsBeforeOrderByStartDesc(anyLong(), any(Instant.class)))
                    .thenReturn(bookings);

            List<BookingDto> result = bookingService.getAllByOwner(owner.getId(), BookingStatusView.PAST);

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(bookingDto, result.getFirst());
            verify(userRepository).findById(anyLong());
            verify(repository).findAllByItem_Owner_IdAndEndIsBeforeOrderByStartDesc(anyLong(), any(Instant.class));
            mockedMapper.verify(() -> BookingMapper.toBookingDto(booking));
        }
    }

    @Test
    void getAllByOwner_shouldReturnFutureBookings() {
        BookingDto bookingDto = new BookingDto(1L, LocalDateTime.now().plusSeconds(3600), LocalDateTime.now().plusSeconds(7200),
                BookingStatus.WAITING, null, null);
        List<Booking> bookings = List.of(booking);

        try (MockedStatic<BookingMapper> mockedMapper = mockStatic(BookingMapper.class);
             MockedStatic<DateTimeUtils> mockedDateTimeUtils = mockStatic(DateTimeUtils.class)) {

            mockedMapper.when(() -> BookingMapper.toBookingDto(any(Booking.class)))
                    .thenReturn(bookingDto);
            mockedDateTimeUtils.when(() -> DateTimeUtils.toUTC(any(LocalDateTime.class)))
                    .thenReturn(Instant.now());

            when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
            when(repository.findAllByItem_Owner_IdAndStartIsAfterOrderByStartDesc(anyLong(), any(Instant.class)))
                    .thenReturn(bookings);

            List<BookingDto> result = bookingService.getAllByOwner(owner.getId(), BookingStatusView.FUTURE);

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(bookingDto, result.getFirst());
            verify(userRepository).findById(anyLong());
            verify(repository).findAllByItem_Owner_IdAndStartIsAfterOrderByStartDesc(anyLong(), any(Instant.class));
            mockedMapper.verify(() -> BookingMapper.toBookingDto(booking));
        }
    }

    @Test
    void getAllByOwner_shouldReturnWaitingBookings() {
        BookingDto bookingDto = new BookingDto(1L, LocalDateTime.now().plusSeconds(3600), LocalDateTime.now().plusSeconds(7200),
                BookingStatus.WAITING, null, null);
        List<Booking> bookings = List.of(booking);

        try (MockedStatic<BookingMapper> mockedMapper = mockStatic(BookingMapper.class)) {
            mockedMapper.when(() -> BookingMapper.toBookingDto(any(Booking.class)))
                    .thenReturn(bookingDto);

            when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
            when(repository.findAllByItem_Owner_IdAndStatusOrderByStartDesc(anyLong(), any(BookingStatus.class)))
                    .thenReturn(bookings);

            List<BookingDto> result = bookingService.getAllByOwner(owner.getId(), BookingStatusView.WAITING);

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(bookingDto, result.getFirst());
            verify(userRepository).findById(anyLong());
            verify(repository).findAllByItem_Owner_IdAndStatusOrderByStartDesc(anyLong(), eq(BookingStatus.WAITING));
            mockedMapper.verify(() -> BookingMapper.toBookingDto(booking));
        }
    }

    @Test
    void getAllByOwner_shouldReturnRejectedBookings() {
        BookingDto bookingDto = new BookingDto(1L, LocalDateTime.now().plusSeconds(3600), LocalDateTime.now().plusSeconds(7200),
                BookingStatus.REJECTED, null, null);
        List<Booking> bookings = List.of(booking);

        try (MockedStatic<BookingMapper> mockedMapper = mockStatic(BookingMapper.class)) {
            mockedMapper.when(() -> BookingMapper.toBookingDto(any(Booking.class)))
                    .thenReturn(bookingDto);

            when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
            when(repository.findAllByItem_Owner_IdAndStatusOrderByStartDesc(anyLong(), any(BookingStatus.class)))
                    .thenReturn(bookings);

            List<BookingDto> result = bookingService.getAllByOwner(owner.getId(), BookingStatusView.REJECTED);

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(bookingDto, result.getFirst());
            verify(userRepository).findById(anyLong());
            verify(repository).findAllByItem_Owner_IdAndStatusOrderByStartDesc(anyLong(), eq(BookingStatus.REJECTED));
            mockedMapper.verify(() -> BookingMapper.toBookingDto(booking));
        }
    }

    @Test
    void getAllByOwner_shouldReturnAllBookings() {
        BookingDto bookingDto = new BookingDto(1L, LocalDateTime.now().plusSeconds(3600), LocalDateTime.now().plusSeconds(7200),
                BookingStatus.WAITING, null, null);
        List<Booking> bookings = List.of(booking);

        try (MockedStatic<BookingMapper> mockedMapper = mockStatic(BookingMapper.class)) {
            mockedMapper.when(() -> BookingMapper.toBookingDto(any(Booking.class)))
                    .thenReturn(bookingDto);

            when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
            when(repository.findAllByItem_Owner_IdOrderByStartDesc(anyLong()))
                    .thenReturn(bookings);

            List<BookingDto> result = bookingService.getAllByOwner(owner.getId(), BookingStatusView.ALL);

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(bookingDto, result.getFirst());
            verify(userRepository).findById(anyLong());
            verify(repository).findAllByItem_Owner_IdOrderByStartDesc(2L);
            mockedMapper.verify(() -> BookingMapper.toBookingDto(booking));
        }
    }

    @Test
    void getAllByOwner_shouldThrowNotFoundExceptionWhenUserNotFound() {
        Long userId = 999L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.getAllByOwner(userId, BookingStatusView.ALL));
        verify(userRepository).findById(userId);
        verifyNoInteractions(repository);
    }
}