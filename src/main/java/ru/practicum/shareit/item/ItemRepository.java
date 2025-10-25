package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findAllByOwnerId(long id);

    @Query("""
            SELECT i FROM Item i
            WHERE available = true AND (LOWER(i.name) LIKE LOWER(CONCAT('%', :text, '%'))
               OR LOWER(i.description) LIKE LOWER(CONCAT('%', :text, '%')))
            """)
    List<Item> searchByText(@Param("text") String text);
}
