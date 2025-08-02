package ru.practicum.shareit.item.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findAllByOwner_Id(long ownerId);

    List<Item> findAllByRequest_IdIn(List<Long> requestIds);

    @Query("SELECT i from Item i WHERE i.available = true AND (" +
            "LOWER(i.name) LIKE LOWER(CONCAT('%', :text, '%')) OR " +
            "LOWER(i.description) LIKE LOWER(CONCAT('%', :text, '%'))" +
            ")")
    List<Item> searchAllByText(String text);

}
