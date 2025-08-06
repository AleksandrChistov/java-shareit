package ru.practicum.shareit.item.utils;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.model.Item;

import static org.junit.jupiter.api.Assertions.*;

class ItemUtilsTest {

    @Test
    void updateItem_shouldUpdateAllFieldsWhenTheyAreNotNullAndNotBlank() {
        Item oldItem = new Item();
        oldItem.setName("Old Name");
        oldItem.setDescription("Old Description");
        oldItem.setAvailable(false);

        UpdateItemDto newItemDto = new UpdateItemDto(1L, "New Name", "New Description", true, 1L);

        Item result = ItemUtils.updateItem(oldItem, newItemDto);

        assertNotNull(result);
        assertEquals("New Name", result.getName());
        assertEquals("New Description", result.getDescription());
        assertTrue(result.getAvailable());
        assertSame(oldItem, result);
    }

    @Test
    void updateItem_shouldUpdateOnlyNameWhenOtherFieldsAreNull() {
        Item oldItem = new Item();
        oldItem.setName("Old Name");
        oldItem.setDescription("Old Description");
        oldItem.setAvailable(false);

        UpdateItemDto newItemDto = new UpdateItemDto(1L, "New Name", null, null, null);

        Item result = ItemUtils.updateItem(oldItem, newItemDto);

        assertNotNull(result);
        assertEquals("New Name", result.getName());
        assertEquals("Old Description", result.getDescription());
        assertFalse(result.getAvailable());
    }

    @Test
    void updateItem_shouldUpdateOnlyDescriptionWhenOtherFieldsAreNull() {
        Item oldItem = new Item();
        oldItem.setName("Old Name");
        oldItem.setDescription("Old Description");
        oldItem.setAvailable(false);

        UpdateItemDto newItemDto = new UpdateItemDto(1L, null, "New Description", null, null);

        Item result = ItemUtils.updateItem(oldItem, newItemDto);

        assertNotNull(result);
        assertEquals("Old Name", result.getName());
        assertEquals("New Description", result.getDescription());
        assertFalse(result.getAvailable());
    }

    @Test
    void updateItem_shouldUpdateOnlyAvailableWhenOtherFieldsAreNull() {
        Item oldItem = new Item();
        oldItem.setName("Old Name");
        oldItem.setDescription("Old Description");
        oldItem.setAvailable(false);

        UpdateItemDto newItemDto = new UpdateItemDto(1L, null, null, true, null);

        Item result = ItemUtils.updateItem(oldItem, newItemDto);

        assertNotNull(result);
        assertEquals("Old Name", result.getName());
        assertEquals("Old Description", result.getDescription());
        assertTrue(result.getAvailable());
    }

    @Test
    void updateItem_shouldNotUpdateNameWhenItIsBlank() {
        Item oldItem = new Item();
        oldItem.setName("Old Name");
        oldItem.setDescription("Old Description");
        oldItem.setAvailable(false);

        UpdateItemDto newItemDto = new UpdateItemDto(1L, "   ", "New Description", true, 1L);

        Item result = ItemUtils.updateItem(oldItem, newItemDto);

        assertNotNull(result);
        assertEquals("Old Name", result.getName());
        assertEquals("New Description", result.getDescription());
        assertTrue(result.getAvailable());
    }

    @Test
    void updateItem_shouldNotUpdateDescriptionWhenItIsBlank() {
        Item oldItem = new Item();
        oldItem.setName("Old Name");
        oldItem.setDescription("Old Description");
        oldItem.setAvailable(false);

        UpdateItemDto newItemDto = new UpdateItemDto(1L, "New Name", "   ", true, 1L);

        Item result = ItemUtils.updateItem(oldItem, newItemDto);

        assertNotNull(result);
        assertEquals("New Name", result.getName());
        assertEquals("Old Description", result.getDescription()); // Не изменилось
        assertTrue(result.getAvailable());
    }

    @Test
    void updateItem_shouldNotUpdateAnyFieldWhenDtoHasOnlyNulls() {
        Item oldItem = new Item();
        oldItem.setName("Old Name");
        oldItem.setDescription("Old Description");
        oldItem.setAvailable(true);

        UpdateItemDto newItemDto = new UpdateItemDto(1L, null, null, null, null);

        Item result = ItemUtils.updateItem(oldItem, newItemDto);

        assertNotNull(result);
        assertEquals("Old Name", result.getName());
        assertEquals("Old Description", result.getDescription());
        assertTrue(result.getAvailable());
    }

}