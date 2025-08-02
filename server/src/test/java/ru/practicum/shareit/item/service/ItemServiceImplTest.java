package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.view.BookingDatesView;
import ru.practicum.shareit.core.error.exception.LackOfRightsException;
import ru.practicum.shareit.core.error.exception.NotAvailableException;
import ru.practicum.shareit.core.error.exception.NotFoundException;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.utils.ItemUtils;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.share.util.DateTimeUtils;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    private User owner;
    private Item item;
    private ItemRequest itemRequest;
    private Comment comment;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setId(1L);
        owner.setName("Test Owner");
        owner.setEmail("owner@example.com");

        itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("Test request");

        item = new Item();
        item.setId(1L);
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setOwner(owner);
        item.setRequest(itemRequest);

        comment = new Comment();
        comment.setId(1L);
        comment.setText("Test comment");
        comment.setItem(item);
        comment.setAuthor(owner);
        comment.setCreatedOn(Instant.now());
    }

    @Test
    void getAllByOwner_shouldReturnListOfFullItemDtos() {
        Long ownerId = 1L;
        FullItemDto fullItemDto = new FullItemDto(1L, "Test Item", "Test Description", true,
                LocalDateTime.now().minusSeconds(3600), LocalDateTime.now().plusSeconds(3600), 1L, List.of());
        BookingDatesView bookingDatesView = mock(BookingDatesView.class);

        try (MockedStatic<ItemMapper> mockedMapper = mockStatic(ItemMapper.class)) {
            mockedMapper.when(() -> ItemMapper.toFullItemDto(any(Item.class), any(), any(), anyList()))
                    .thenReturn(fullItemDto);

            when(bookingRepository.findLastAndNextBookingDatesByOwnerId(ownerId))
                    .thenReturn(List.of(bookingDatesView));
            when(itemRepository.findAllByOwner_Id(ownerId)).thenReturn(List.of(item));
            when(commentRepository.findAllByItem_IdIn(anyList())).thenReturn(List.of());

            List<FullItemDto> result = itemService.getAllByOwner(ownerId);

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(fullItemDto, result.getFirst());
            verify(bookingRepository).findLastAndNextBookingDatesByOwnerId(ownerId);
            verify(itemRepository).findAllByOwner_Id(ownerId);
            mockedMapper.verify(() -> ItemMapper.toFullItemDto(any(Item.class), any(), any(), anyList()));
        }
    }

    @Test
    void getAllByOwner_shouldReturnEmptyListWhenNoItems() {
        Long ownerId = 1L;

        when(bookingRepository.findLastAndNextBookingDatesByOwnerId(ownerId)).thenReturn(List.of());
        when(itemRepository.findAllByOwner_Id(ownerId)).thenReturn(List.of());

        List<FullItemDto> result = itemService.getAllByOwner(ownerId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(bookingRepository).findLastAndNextBookingDatesByOwnerId(ownerId);
        verify(itemRepository).findAllByOwner_Id(ownerId);
    }

    @Test
    void getById_shouldReturnFullItemDto() {
        Long itemId = 1L;
        FullItemDto fullItemDto = new FullItemDto(1L, "Test Item", "Test Description", true,
                null, null, 1L, List.of());

        try (MockedStatic<ItemMapper> mockedMapper = mockStatic(ItemMapper.class)) {
            mockedMapper.when(() -> ItemMapper.toFullItemDto(any(Item.class), any(), any(), anyList()))
                    .thenReturn(fullItemDto);

            when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
            when(commentRepository.findAllByItem_IdIn(anyList())).thenReturn(List.of());

            FullItemDto result = itemService.getById(itemId);

            assertNotNull(result);
            assertEquals(fullItemDto, result);
            verify(itemRepository).findById(itemId);
            verify(commentRepository).findAllByItem_IdIn(anyList());
            mockedMapper.verify(() -> ItemMapper.toFullItemDto(any(Item.class), any(), any(), anyList()));
        }
    }

    @Test
    void getById_shouldThrowNotFoundExceptionWhenItemNotFound() {
        Long itemId = 999L;

        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.getById(itemId));
        verify(itemRepository).findById(itemId);
    }

    @Test
    void searchAll_shouldReturnListOfItemDtos() {
        String text = "test";
        ItemDto itemDto = new ItemDto(1L, "Test Item", "Test Description", true, 1L);

        try (MockedStatic<ItemMapper> mockedMapper = mockStatic(ItemMapper.class)) {
            mockedMapper.when(() -> ItemMapper.toItemDto(any(Item.class))).thenReturn(itemDto);

            when(itemRepository.searchAllByText(text)).thenReturn(List.of(item));

            List<ItemDto> result = itemService.searchAll(text);

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(itemDto, result.getFirst());
            verify(itemRepository).searchAllByText(text);
            mockedMapper.verify(() -> ItemMapper.toItemDto(item));
        }
    }

    @Test
    void searchAll_shouldReturnEmptyListWhenTextIsBlank() {
        String text = "   ";

        List<ItemDto> result = itemService.searchAll(text);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void searchAll_shouldReturnEmptyListWhenNoItemsFound() {
        String text = "nonexistent";

        when(itemRepository.searchAllByText(text)).thenReturn(List.of());

        List<ItemDto> result = itemService.searchAll(text);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(itemRepository).searchAllByText(text);
    }

    @Test
    void create_shouldReturnItemDto() {
        Long ownerId = 1L;
        Long requestId = 1L;
        ItemDto itemDto = new ItemDto(null, "Test Item", "Test Description", true, requestId);
        ItemDto createdItemDto = new ItemDto(1L, "Test Item", "Test Description", true, requestId);

        try (MockedStatic<ItemMapper> mockedMapper = mockStatic(ItemMapper.class)) {
            mockedMapper.when(() -> ItemMapper.toItem(any(ItemDto.class), any(User.class), any(ItemRequest.class)))
                    .thenReturn(item);
            mockedMapper.when(() -> ItemMapper.toItemDto(any(Item.class))).thenReturn(createdItemDto);

            when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
            when(itemRequestRepository.findById(requestId)).thenReturn(Optional.of(itemRequest));
            when(itemRepository.save(any(Item.class))).thenReturn(item);

            ItemDto result = itemService.create(ownerId, itemDto);

            assertNotNull(result);
            assertEquals(createdItemDto, result);
            verify(userRepository).findById(ownerId);
            verify(itemRequestRepository).findById(requestId);
            verify(itemRepository).save(any(Item.class));
            mockedMapper.verify(() -> ItemMapper.toItem(any(ItemDto.class), any(User.class), any(ItemRequest.class)));
            mockedMapper.verify(() -> ItemMapper.toItemDto(item));
        }
    }

    @Test
    void create_shouldReturnItemDtoWhenRequestIdIsNull() {
        Long ownerId = 1L;
        ItemDto itemDto = new ItemDto(null, "Test Item", "Test Description", true, null);
        ItemDto createdItemDto = new ItemDto(1L, "Test Item", "Test Description", true, null);

        try (MockedStatic<ItemMapper> mockedMapper = mockStatic(ItemMapper.class)) {
            mockedMapper.when(() -> ItemMapper.toItem(any(ItemDto.class), any(User.class), any()))
                    .thenReturn(item);
            mockedMapper.when(() -> ItemMapper.toItemDto(any(Item.class))).thenReturn(createdItemDto);

            when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
            when(itemRepository.save(any(Item.class))).thenReturn(item);

            ItemDto result = itemService.create(ownerId, itemDto);

            assertNotNull(result);
            assertEquals(createdItemDto, result);
            verify(userRepository).findById(ownerId);
            verifyNoInteractions(itemRequestRepository);
            verify(itemRepository).save(any(Item.class));
            mockedMapper.verify(() -> ItemMapper.toItem(any(ItemDto.class), any(User.class), isNull()));
            mockedMapper.verify(() -> ItemMapper.toItemDto(item));
        }
    }

    @Test
    void create_shouldThrowNotFoundExceptionWhenUserNotFound() {
        Long ownerId = 999L;
        ItemDto itemDto = new ItemDto(null, "Test Item", "Test Description", true, null);

        when(userRepository.findById(ownerId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.create(ownerId, itemDto));
        verify(userRepository).findById(ownerId);
        verifyNoInteractions(itemRepository);
    }

    @Test
    void create_shouldThrowNotFoundExceptionWhenRequestNotFound() {
        Long ownerId = 1L;
        Long requestId = 999L;
        ItemDto itemDto = new ItemDto(null, "Test Item", "Test Description", true, requestId);

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.create(ownerId, itemDto));
        verify(userRepository).findById(ownerId);
        verify(itemRequestRepository).findById(requestId);
        verifyNoInteractions(itemRepository);
    }

    @Test
    void update_shouldReturnItemDto() {
        Long ownerId = 1L;
        Long itemId = 1L;
        UpdateItemDto updateItemDto = new UpdateItemDto(itemId, "Updated Item", "Updated Description", false, 1L);
        ItemDto updatedItemDto = new ItemDto(itemId, "Updated Item", "Updated Description", false, 1L);

        try (MockedStatic<ItemUtils> mockedUtils = mockStatic(ItemUtils.class);
             MockedStatic<ItemMapper> mockedMapper = mockStatic(ItemMapper.class)) {

            mockedUtils.when(() -> ItemUtils.updateItem(any(Item.class), any(UpdateItemDto.class)))
                    .thenReturn(item);
            mockedMapper.when(() -> ItemMapper.toItemDto(any(Item.class))).thenReturn(updatedItemDto);

            when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
            when(itemRepository.save(any(Item.class))).thenReturn(item);

            ItemDto result = itemService.update(ownerId, itemId, updateItemDto);

            assertNotNull(result);
            assertEquals(updatedItemDto, result);
            verify(itemRepository).findById(itemId);
            mockedUtils.verify(() -> ItemUtils.updateItem(item, updateItemDto));
            verify(itemRepository).save(item);
            mockedMapper.verify(() -> ItemMapper.toItemDto(item));
        }
    }

    @Test
    void update_shouldThrowNotFoundExceptionWhenItemNotFound() {
        Long ownerId = 1L;
        Long itemId = 999L;
        UpdateItemDto updateItemDto = new UpdateItemDto(itemId, "Updated Item", "Updated Description", false, 1L);

        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.update(ownerId, itemId, updateItemDto));
        verify(itemRepository).findById(itemId);
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void update_shouldThrowLackOfRightsExceptionWhenUserIsNotOwner() {
        Long ownerId = 1L;
        Long itemId = 1L;
        UpdateItemDto updateItemDto = new UpdateItemDto(itemId, "Updated Item", "Updated Description", false, 1L);

        User anotherUser = new User();
        anotherUser.setId(2L);
        item.setOwner(anotherUser);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertThrows(LackOfRightsException.class, () -> itemService.update(ownerId, itemId, updateItemDto));
        verify(itemRepository).findById(itemId);
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void addComment_shouldReturnCommentDto() {
        Long userId = 1L;
        Long itemId = 1L;
        CreateCommentDto createCommentDto = new CreateCommentDto("Test comment");
        CommentDto commentDto = new CommentDto(1L, "Test comment", "Test Author", Instant.now());
        Booking booking = new Booking();
        booking.setBooker(owner);

        try (MockedStatic<CommentMapper> mockedCommentMapper = mockStatic(CommentMapper.class);
             MockedStatic<DateTimeUtils> mockedDateTimeUtils = mockStatic(DateTimeUtils.class)) {

            mockedCommentMapper.when(() -> CommentMapper.toComment(any(CreateCommentDto.class), any(Item.class), any(User.class)))
                    .thenReturn(comment);
            mockedCommentMapper.when(() -> CommentMapper.toCommentDto(any(Comment.class))).thenReturn(commentDto);
            mockedDateTimeUtils.when(() -> DateTimeUtils.toUTC(any(LocalDateTime.class)))
                    .thenReturn(Instant.now());

            when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
            when(bookingRepository.findAllByItem_IdAndBooker_IdAndEndIsBefore(anyLong(), anyLong(), any(Instant.class)))
                    .thenReturn(List.of(booking));
            when(commentRepository.save(any(Comment.class))).thenReturn(comment);

            CommentDto result = itemService.addComment(userId, itemId, createCommentDto);

            assertNotNull(result);
            assertEquals(commentDto, result);
            verify(itemRepository).findById(itemId);
            verify(bookingRepository).findAllByItem_IdAndBooker_IdAndEndIsBefore(anyLong(), anyLong(), any(Instant.class));
            mockedCommentMapper.verify(() -> CommentMapper.toComment(any(CreateCommentDto.class), any(Item.class), any(User.class)));
            verify(commentRepository).save(any(Comment.class));
            mockedCommentMapper.verify(() -> CommentMapper.toCommentDto(comment));
        }
    }

    @Test
    void addComment_shouldThrowNotFoundExceptionWhenItemNotFound() {
        Long userId = 1L;
        Long itemId = 999L;
        CreateCommentDto createCommentDto = new CreateCommentDto("Test comment");

        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.addComment(userId, itemId, createCommentDto));
        verify(itemRepository).findById(itemId);
        verifyNoInteractions(commentRepository);
    }

    @Test
    void addComment_shouldThrowNotAvailableExceptionWhenUserDidNotBookItem() {
        Long userId = 1L;
        Long itemId = 1L;
        CreateCommentDto createCommentDto = new CreateCommentDto("Test comment");

        try (MockedStatic<DateTimeUtils> mockedDateTimeUtils = mockStatic(DateTimeUtils.class)) {
            mockedDateTimeUtils.when(() -> DateTimeUtils.toUTC(any(LocalDateTime.class)))
                    .thenReturn(Instant.now());

            when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
            when(bookingRepository.findAllByItem_IdAndBooker_IdAndEndIsBefore(anyLong(), anyLong(), any(Instant.class)))
                    .thenReturn(List.of());

            assertThrows(NotAvailableException.class, () -> itemService.addComment(userId, itemId, createCommentDto));
            verify(itemRepository).findById(itemId);
            verify(bookingRepository).findAllByItem_IdAndBooker_IdAndEndIsBefore(anyLong(), anyLong(), any(Instant.class));
        }
    }

}