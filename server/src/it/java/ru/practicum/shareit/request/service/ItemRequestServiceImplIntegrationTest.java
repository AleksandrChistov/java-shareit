package ru.practicum.shareit.request.service;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.core.error.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class ItemRequestServiceImplIntegrationTest {

    private final EntityManager em;
    private final ItemRequestService itemRequestService;

    private Long requestorId;
    private Long requestId;
    private Long ownerId;
    private User requestor;
    private ItemRequest request;

    @BeforeEach
    void setUp() {
        requestor = new User();
        requestor.setName("Requestor");
        requestor.setEmail("requestor@example.com");
        em.persist(requestor);
        requestorId = requestor.getId();

        User owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@example.com");
        em.persist(owner);
        ownerId = owner.getId();

        request = new ItemRequest();
        request.setDescription("Хочу арендовать дрель");
        request.setRequestor(requestor);
        em.persist(request);
        requestId = request.getId();

        Item item1 = new Item();
        item1.setName("Дрель");
        item1.setDescription("Простая дрель");
        item1.setAvailable(true);
        item1.setOwner(owner);
        item1.setRequest(request);
        em.persist(item1);

        Item item2 = new Item();
        item2.setName("Шуруповерт");
        item2.setDescription("Аккумуляторный шуруповерт");
        item2.setAvailable(true);
        item2.setOwner(owner);
        item2.setRequest(request);
        em.persist(item2);

        em.flush();
    }

    @Test
    void getById_shouldReturnRequestWithItems() {
        // Получаем запрос с вещами по владельцу вещей
        ItemRequestWithItemsDto result = itemRequestService.getById(ownerId, requestId);

        assertNotNull(result);
        assertEquals(requestId, result.getId());
        assertEquals("Хочу арендовать дрель", result.getDescription());
        assertNotNull(result.getCreated());
        assertEquals(requestorId, result.getRequestorId());

        assertNotNull(result.getItems());
        assertEquals(2, result.getItems().size());

        List<String> itemNames = result.getItems().stream()
                .map(ItemDto::getName)
                .toList();
        assertTrue(itemNames.contains("Дрель"));
        assertTrue(itemNames.contains("Шуруповерт"));
    }

    @Test
    void getById_shouldReturnRequestWithEmptyItemsWhenNoItemsFound() {
        // Создаем запрос без связанных вещей
        ItemRequest anotherRequest = new ItemRequest();
        anotherRequest.setDescription("Хочу арендовать молоток");
        anotherRequest.setRequestor(requestor);
        em.persist(anotherRequest);
        em.flush();

        Long anotherRequestId = anotherRequest.getId();

        ItemRequestWithItemsDto result = itemRequestService.getById(requestorId, anotherRequestId);

        assertNotNull(result);
        assertEquals(anotherRequestId, result.getId());
        assertEquals("Хочу арендовать молоток", result.getDescription());
        assertNotNull(result.getCreated());
        assertEquals(requestorId, result.getRequestorId());

        assertNotNull(result.getItems());
        assertTrue(result.getItems().isEmpty());
    }

    @Test
    void getById_shouldThrowNotFoundExceptionWhenRequestNotFound() {
        Long nonExistentRequestId = 999L;

        assertThrows(NotFoundException.class, () -> itemRequestService.getById(requestorId, nonExistentRequestId));
    }

    @Test
    void getById_shouldReturnRequestEvenWhenUserIsNotRequestor() {
        // Проверяем, что любой пользователь может получить информацию о запросе
        ItemRequestWithItemsDto result = itemRequestService.getById(ownerId, requestId);

        assertNotNull(result);
        assertEquals(requestId, result.getId());
        assertEquals("Хочу арендовать дрель", result.getDescription());

        assertNotNull(result.getItems());
        assertEquals(2, result.getItems().size());
    }

    @Test
    void getById_shouldReturnItemsBelongingToSpecifiedUser() {
        User thirdUser = new User();
        thirdUser.setName("Third User");
        thirdUser.setEmail("third@example.com");
        em.persist(thirdUser);

        // Создаем вещь, принадлежащую третьему пользователю, но связанную с тем же запросом
        Item item3 = new Item();
        item3.setName("Перфоратор");
        item3.setDescription("Мощный перфоратор");
        item3.setAvailable(true);
        item3.setOwner(thirdUser);
        item3.setRequest(request);
        em.persist(item3);

        em.flush();

        Long thirdUserId = thirdUser.getId();

        ItemRequestWithItemsDto result = itemRequestService.getById(thirdUserId, requestId);

        assertNotNull(result.getItems());
        assertEquals(1, result.getItems().size());
        assertEquals("Перфоратор", result.getItems().getFirst().getName());
    }

}