package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.enums.BookingStatusView;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private BookingController bookingController;

    private MockMvc mvc;

    private BookingDto bookingDto;
    private CreateBookingDto createBookingDto;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(bookingController)
                .build();

        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Создание тестовых данных
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        createBookingDto = new CreateBookingDto(1L, start, end);

        UserDto bookerDto = new UserDto(1L, "Booker", "booker@example.com");
        bookingDto = new BookingDto(1L, start, end, BookingStatus.WAITING, bookerDto, null);
    }

    @Test
    void create_shouldReturnCreatedBooking() throws Exception {
        when(bookingService.create(any(CreateBookingDto.class), anyLong())).thenReturn(bookingDto);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(createBookingDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("WAITING"))
                .andExpect(jsonPath("$.booker.id").value(1L));
    }

    @Test
    void create_shouldReturnBadRequestWhenHeaderIsMissing() throws Exception {
        mvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createBookingDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void approve_shouldReturnApprovedBooking() throws Exception {
        BookingDto approvedBookingDto = new BookingDto(1L, bookingDto.getStart(), bookingDto.getEnd(),
                BookingStatus.APPROVED, bookingDto.getBooker(), bookingDto.getItem());
        when(bookingService.approve(anyLong(), anyLong(), anyBoolean())).thenReturn(approvedBookingDto);

        mvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void approve_shouldReturnRejectedBooking() throws Exception {
        BookingDto rejectedBookingDto = new BookingDto(1L, bookingDto.getStart(), bookingDto.getEnd(),
                BookingStatus.REJECTED, bookingDto.getBooker(), bookingDto.getItem());
        when(bookingService.approve(anyLong(), anyLong(), anyBoolean())).thenReturn(rejectedBookingDto);

        mvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", "false"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("REJECTED"));
    }

    @Test
    void approve_shouldReturnBadRequestWhenHeaderIsMissing() throws Exception {
        mvc.perform(patch("/bookings/1")
                        .param("approved", "true"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getById_shouldReturnBooking() throws Exception {
        when(bookingService.getById(anyLong(), anyLong())).thenReturn(bookingDto);

        mvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("WAITING"));
    }

    @Test
    void getById_shouldReturnBadRequestWhenHeaderIsMissing() throws Exception {
        mvc.perform(get("/bookings/1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllByBooker_shouldReturnBookingsList() throws Exception {
        BookingDto pastBookingDto = new BookingDto(2L, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1),
                BookingStatus.APPROVED, bookingDto.getBooker(), null);
        when(bookingService.getAllByBooker(anyLong(), eq(BookingStatusView.PAST))).thenReturn(List.of(pastBookingDto));

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "PAST"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(2L))
                .andExpect(jsonPath("$[0].status").value("APPROVED"));
    }

    @Test
    void getAllByBooker_shouldReturnBookingsListWithDefaultState() throws Exception {
        when(bookingService.getAllByBooker(anyLong(), any(BookingStatusView.class))).thenReturn(List.of(bookingDto));

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void getAllByBooker_shouldReturnBadRequestWhenHeaderIsMissing() throws Exception {
        mvc.perform(get("/bookings")
                        .param("state", "ALL"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllByOwner_shouldReturnBookingsList() throws Exception {
        BookingDto futureBookingDto = new BookingDto(3L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                BookingStatus.WAITING, bookingDto.getBooker(), null);
        when(bookingService.getAllByOwner(anyLong(), eq(BookingStatusView.FUTURE))).thenReturn(List.of(futureBookingDto));

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "FUTURE"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(3L))
                .andExpect(jsonPath("$[0].status").value("WAITING"));
    }

    @Test
    void getAllByOwner_shouldReturnBookingsListWithDefaultState() throws Exception {
        when(bookingService.getAllByOwner(anyLong(), any(BookingStatusView.class))).thenReturn(List.of(bookingDto));

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getAllByOwner_shouldReturnBadRequestWhenHeaderIsMissing() throws Exception {
        mvc.perform(get("/bookings/owner")
                        .param("state", "ALL"))
                .andExpect(status().isBadRequest());
    }

}
