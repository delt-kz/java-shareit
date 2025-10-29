package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;
import java.util.Optional;


public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("""
                SELECT c
                FROM Comment c
                JOIN FETCH c.author
                WHERE c.item.id = :itemId
                ORDER BY c.created DESC
            """)
    List<Comment> getItemComment(Long itemId);


    @Query("""
                SELECT c
                FROM Comment c
                JOIN FETCH c.author
                WHERE c.item.id IN :itemIds
                ORDER BY c.created DESC
            """)
    List<Comment> findAllByItemIdIn(List<Long> itemIds);

    @Query("""
                SELECT COUNT(b) > 0
                FROM Booking b
                WHERE b.booker.id = :bookerId
                  AND b.item.id = :itemId
                  AND b.end < CURRENT_TIMESTAMP
                  AND b.status = 'APPROVED'
            """)
    boolean hasUserBookedItem(Long bookerId, Long itemId);


    @Query("""
                SELECT c
                FROM Comment c
                JOIN FETCH c.author
                WHERE c.id = :commentId
            """)
    Optional<Comment> findByIdWithAuthor(Long commentId);
}
