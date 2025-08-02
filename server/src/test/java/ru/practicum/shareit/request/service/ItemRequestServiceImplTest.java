package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.core.error.exception.NotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @Mock
    private ItemRequestRepository repository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    private User user;
    private ItemRequest itemRequest;
    private Item item;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@example.com");

        itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("Test request");
        itemRequest.setRequestor(user);

        item = new Item();
        item.setId(1L);
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setOwner(user);
        item.setRequest(itemRequest);
    }

    @Test
    void getAll_shouldReturnListOfItemRequestDtos() {
        List<ItemRequest> itemRequests = List.of(itemRequest);
        ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "Test request", LocalDateTime.now(), 1L);

        try (MockedStatic<ItemRequestMapper> mockedMapper = mockStatic(ItemRequestMapper.class)) {
            mockedMapper.when(() -> ItemRequestMapper.toItemRequestDto(any(ItemRequest.class)))
                    .thenReturn(itemRequestDto);

            when(repository.findAllByOrderByCreatedDesc()).thenReturn(itemRequests);

            List<ItemRequestDto> result = itemRequestService.getAll();

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(itemRequestDto, result.getFirst());
            verify(repository).findAllByOrderByCreatedDesc();
            mockedMapper.verify(() -> ItemRequestMapper.toItemRequestDto(itemRequest));
        }
    }

    @Test
    void getAllOfOwnerWithItems_shouldReturnListOfItemRequestWithItemsDtos() {
        List<ItemRequest> itemRequests = List.of(itemRequest);
        List<Item> items = List.of(item);
        ItemDto itemDto = new ItemDto(1L, "Test Item", "Test Description", true, 1L);
        ItemRequestWithItemsDto itemRequestWithItemsDto = new ItemRequestWithItemsDto(
                1L, "Test request", LocalDateTime.now(), 1L, List.of(itemDto)
        );

        try (MockedStatic<ItemRequestMapper> mockedRequestMapper = mockStatic(ItemRequestMapper.class);
             MockedStatic<ItemMapper> mockedItemMapper = mockStatic(ItemMapper.class)) {

            mockedRequestMapper.when(() -> ItemRequestMapper.toItemRequestWithItemsDto(any(ItemRequest.class), anyList()))
                    .thenReturn(itemRequestWithItemsDto);

            mockedItemMapper.when(() -> ItemMapper.toItemDto(any(Item.class)))
                    .thenReturn(itemDto);

            when(repository.findAllByRequestor_IdOrderByCreatedDesc(anyLong())).thenReturn(itemRequests);
            when(itemRepository.findAllByRequest_IdIn(anyList())).thenReturn(items);

            List<ItemRequestWithItemsDto> result = itemRequestService.getAllOfOwnerWithItems(1L);

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(itemRequestWithItemsDto, result.getFirst());
            verify(repository).findAllByRequestor_IdOrderByCreatedDesc(1L);
            verify(itemRepository).findAllByRequest_IdIn(anyList());
            mockedItemMapper.verify(() -> ItemMapper.toItemDto(item));
            mockedRequestMapper.verify(() -> ItemRequestMapper.toItemRequestWithItemsDto(itemRequest, List.of(itemDto)));
        }
    }

    @Test
    void getAllOfOwnerWithItems_shouldReturnEmptyListWhenNoRequests() {
        when(repository.findAllByRequestor_IdOrderByCreatedDesc(anyLong())).thenReturn(List.of());

        List<ItemRequestWithItemsDto> result = itemRequestService.getAllOfOwnerWithItems(1L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(repository).findAllByRequestor_IdOrderByCreatedDesc(1L);
        verifyNoInteractions(itemRepository);
    }

    @Test
    void getById_shouldReturnItemRequestWithItemsDtos() {
        List<Item> items = List.of(item);
        ItemDto itemDto = new ItemDto(1L, "Test Item", "Test Description", true, 1L);
        ItemRequestWithItemsDto itemRequestWithItemsDto = new ItemRequestWithItemsDto(
                1L, "Test request", LocalDateTime.now(), 1L, List.of(itemDto)
        );

        try (MockedStatic<ItemRequestMapper> mockedRequestMapper = mockStatic(ItemRequestMapper.class);
             MockedStatic<ItemMapper> mockedItemMapper = mockStatic(ItemMapper.class)) {

            mockedRequestMapper.when(() -> ItemRequestMapper.toItemRequestWithItemsDto(any(ItemRequest.class), anyList()))
                    .thenReturn(itemRequestWithItemsDto);

            mockedItemMapper.when(() -> ItemMapper.toItemDto(any(Item.class)))
                    .thenReturn(itemDto);

            when(repository.findById(anyLong())).thenReturn(Optional.of(itemRequest));
            when(itemRepository.findAllByOwner_Id(anyLong())).thenReturn(items);

            ItemRequestWithItemsDto result = itemRequestService.getById(1L, 1L);

            assertNotNull(result);
            assertEquals(itemRequestWithItemsDto, result);
            verify(repository).findById(1L);
            verify(itemRepository).findAllByOwner_Id(1L);
            mockedItemMapper.verify(() -> ItemMapper.toItemDto(item));
            mockedRequestMapper.verify(() -> ItemRequestMapper.toItemRequestWithItemsDto(itemRequest, List.of(itemDto)));
        }
    }

    @Test
    void getById_shouldThrowNotFoundExceptionWhenRequestNotFound() {
        long requestId = 999L;
        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.getById(1L, requestId));
        verify(repository).findById(requestId);
    }

    @Test
    void create_shouldReturnItemRequestDto() {
        CreateItemRequestDto createItemRequestDto = new CreateItemRequestDto("Test request");
        ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "Test request", LocalDateTime.now(), 1L);

        try (MockedStatic<ItemRequestMapper> mockedMapper = mockStatic(ItemRequestMapper.class)) {
            mockedMapper.when(() -> ItemRequestMapper.toItemRequest(any(CreateItemRequestDto.class), any(User.class)))
                    .thenReturn(itemRequest);
            mockedMapper.when(() -> ItemRequestMapper.toItemRequestDto(any(ItemRequest.class)))
                    .thenReturn(itemRequestDto);
            when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
            when(ItemRequestMapper.toItemRequest(any(CreateItemRequestDto.class), any(User.class)))
                    .thenReturn(itemRequest);
            when(repository.save(any(ItemRequest.class))).thenReturn(itemRequest);
            when(ItemRequestMapper.toItemRequestDto(any(ItemRequest.class))).thenReturn(itemRequestDto);

            ItemRequestDto result = itemRequestService.create(createItemRequestDto, 1L);

            assertNotNull(result);
            assertEquals(itemRequestDto, result);
            verify(userRepository).findById(1L);
            mockedMapper.verify(() -> ItemRequestMapper.toItemRequest(createItemRequestDto, user));
            verify(repository).save(itemRequest);
            mockedMapper.verify(() -> ItemRequestMapper.toItemRequestDto(itemRequest));
        }
    }

    @Test
    void create_shouldThrowNotFoundExceptionWhenUserNotFound() {
        CreateItemRequestDto createItemRequestDto = new CreateItemRequestDto("Test request");
        long userId = 999L;

        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.create(createItemRequestDto, userId));
        verify(userRepository).findById(userId);
        verifyNoInteractions(repository);
    }

}