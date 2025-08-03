package ru.practicum.shareit.item.service;

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
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class ItemServiceImplIntegrationTest {

    private final EntityManager em;
    private final ItemService itemService;

    private Long ownerId;
    private Long requestId;

    @BeforeEach
    void setUp() {
        User owner = new User();
        owner.setName("Item Owner");
        owner.setEmail("owner@example.com");
        em.persist(owner);
        ownerId = owner.getId();

        ItemRequest request = new ItemRequest();
        request.setDescription("Хочу арендовать дрель");
        request.setRequestor(owner);
        em.persist(request);
        requestId = request.getId();

        em.flush(); // Фиксируем изменения в БД
    }

    @Test
    void create_shouldCreateItemWithoutItemRequest() {
        ItemDto itemDto = new ItemDto(null, "Дрель", "Простая дрель", true, null);

        ItemDto createdItem = itemService.create(ownerId, itemDto);

        assertNotNull(createdItem);
        assertNotNull(createdItem.getId());
        assertEquals("Дрель", createdItem.getName());
        assertEquals("Простая дрель", createdItem.getDescription());
        assertTrue(createdItem.getAvailable());
        assertNull(createdItem.getRequestId());
    }

    @Test
    void create_shouldCreateItemWithItemRequest() {
        ItemDto itemDto = new ItemDto(null, "Дрель", "Простая дрель", true, requestId);

        ItemDto createdItem = itemService.create(ownerId, itemDto);

        assertNotNull(createdItem);
        assertNotNull(createdItem.getId());
        assertEquals("Дрель", createdItem.getName());
        assertEquals("Простая дрель", createdItem.getDescription());
        assertTrue(createdItem.getAvailable());
        assertEquals(requestId, createdItem.getRequestId());
    }

    @Test
    void create_shouldThrowNotFoundExceptionWhenOwnerNotFound() {
        Long nonExistentOwnerId = 999L;
        ItemDto itemDto = new ItemDto(null, "Дрель", "Простая дрель", true, null);

        assertThrows(NotFoundException.class, () -> itemService.create(nonExistentOwnerId, itemDto));
    }

    @Test
    void create_shouldThrowNotFoundExceptionWhenRequestNotFound() {
        Long nonExistentRequestId = 999L;
        ItemDto itemDto = new ItemDto(null, "Дрель", "Простая дрель", true, nonExistentRequestId);

        assertThrows(NotFoundException.class, () -> itemService.create(ownerId, itemDto));
    }

    @Test
    void create_shouldPersistItemInDatabase() {
        ItemDto itemDto = new ItemDto(null, "Дрель", "Простая дрель", true, null);

        ItemDto createdItem = itemService.create(ownerId, itemDto);

        em.flush(); // Фиксируем изменения в БД
        em.clear(); // Очищаем persistence context

        var persistedItem = em.find(Item.class, createdItem.getId());

        assertNotNull(persistedItem);
        assertEquals("Дрель", persistedItem.getName());
        assertEquals("Простая дрель", persistedItem.getDescription());
        assertTrue(persistedItem.getAvailable());
        assertEquals(ownerId, persistedItem.getOwner().getId());
        assertNull(persistedItem.getRequest());
    }
}