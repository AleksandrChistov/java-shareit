package ru.practicum.shareit.request;

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
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestControllerTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private ItemRequestService itemRequestService;

    @InjectMocks
    private ItemRequestController itemRequestController;

    private MockMvc mvc;

    private ItemRequestDto itemRequestDto;
    private ItemRequestWithItemsDto itemRequestWithItemsDto;
    private CreateItemRequestDto createItemRequestDto;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(itemRequestController)
                .build();

        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Создание тестовых данных
        createItemRequestDto = new CreateItemRequestDto("Хочу арендовать дрель");
        itemRequestDto = new ItemRequestDto(1L, "Хочу арендовать дрель", LocalDateTime.now(), 1L);
        itemRequestWithItemsDto = new ItemRequestWithItemsDto(1L, "Хочу арендовать дрель", LocalDateTime.now(), 1L, List.of());
    }

    @Test
    void getAllOfOwnerWithItems_shouldReturnRequestsList() throws Exception {
        when(itemRequestService.getAllOfOwnerWithItems(anyLong())).thenReturn(List.of(itemRequestWithItemsDto));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].description").value("Хочу арендовать дрель"));
    }

    @Test
    void getAllOfOwnerWithItems_shouldReturnBadRequestWhenHeaderIsMissing() throws Exception {
        mvc.perform(get("/requests"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAll_shouldReturnAllRequests() throws Exception {
        when(itemRequestService.getAll()).thenReturn(List.of(itemRequestDto));

        mvc.perform(get("/requests/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].description").value("Хочу арендовать дрель"));
    }

    @Test
    void getById_shouldReturnRequestWithItems() throws Exception {
        when(itemRequestService.getById(anyLong(), anyLong())).thenReturn(itemRequestWithItemsDto);

        mvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("Хочу арендовать дрель"))
                .andExpect(jsonPath("$.items").isArray());
    }

    @Test
    void getById_shouldReturnBadRequestWhenHeaderIsMissing() throws Exception {
        mvc.perform(get("/requests/1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getById_shouldReturnBadRequestWhenRequestIdIsInvalid() throws Exception {
        mvc.perform(get("/requests/invalid")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_shouldReturnCreatedRequest() throws Exception {
        when(itemRequestService.create(any(CreateItemRequestDto.class), anyLong())).thenReturn(itemRequestDto);

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(createItemRequestDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("Хочу арендовать дрель"))
                .andExpect(jsonPath("$.requestorId").value(1L));
    }

    @Test
    void create_shouldReturnBadRequestWhenHeaderIsMissing() throws Exception {
        mvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createItemRequestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_shouldReturnBadRequestWhenRequestBodyIsInvalid() throws Exception {
        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("invalid json"))
                .andExpect(status().isBadRequest());
    }
}