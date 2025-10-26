package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("""
            SELECT b FROM Booking b
            JOIN FETCH b.item
            JOIN FETCH b.booker
            WHERE b.id = :id
            """)
    Optional<Booking> findByIdWithRelations(long id);


    @Query(value = """
            SELECT DISTINCT ON (b.item_id) b.*
            FROM bookings b
            WHERE b.item_id IN :itemIds
              AND b.end_date < NOW()
            ORDER BY b.item_id, b.end_date DESC
            """, nativeQuery = true)
    List<Booking> findAllLastBookingsByItemIdIn(List<Long> itemIds);


    @Query(value = """
            SELECT DISTINCT ON (b.item_id) b.*
            FROM bookings b
            WHERE b.item_id IN :itemIds
              AND b.start_date > NOW()
            ORDER BY b.item_id, b.start_date ASC
            """, nativeQuery = true)
    List<Booking> findAllNextBookingsByItemIdIn(List<Long> itemIds);


    @Query(value = """
                SELECT * FROM bookings b
                WHERE b.item_id = :itemId
                  AND b.end_date < NOW()
                ORDER BY b.end_date DESC
                LIMIT 1
            """, nativeQuery = true)
    Booking findLastBookingByItemId(Long itemId);

    @Query(value = """
                SELECT * FROM bookings b
                WHERE b.item_id = :itemId
                  AND b.start_date > NOW()
                ORDER BY b.start_date
                LIMIT 1
            """, nativeQuery = true)
    Booking findNextBookingByItemId(Long itemId);


    @Query("""
            UPDATE Booking b SET b.status = :status WHERE b.id = :id
            """)
    int updateStatus(@Param("status") BookingStatus status, @Param("id") long id);

    List<Booking> findAllByBookerIdOrderByStartDesc(long bookerId);

    @Query("""
            SELECT b FROM Booking b
            WHERE b.booker.id = :bookerId
              AND b.end < CURRENT_TIMESTAMP
            ORDER BY b.start DESC
            """)
    List<Booking> findAllByBookerIdAndStatePast(long bookerId);

    @Query("""
            SELECT b FROM Booking b
            WHERE b.booker.id = :bookerId
              AND b.start <= CURRENT_TIMESTAMP
              AND b.end >= CURRENT_TIMESTAMP
            ORDER BY b.start DESC
            """)
    List<Booking> findAllByBookerIdAndStateCurrent(long bookerId);

    @Query("""
            SELECT b FROM Booking b
            WHERE b.booker.id = :bookerId
              AND b.start > CURRENT_TIMESTAMP
            ORDER BY b.start DESC
            """)
    List<Booking> findAllByBookerIdAndStateFuture(long bookerId);

    @Query("""
            SELECT b FROM Booking b
            WHERE b.booker.id = :bookerId
              AND b.status = 'WAITING'
            ORDER BY b.start DESC
            """)
    List<Booking> findAllByBookerIdAndStateWaiting(long bookerId);

    @Query("""
            SELECT b FROM Booking b
            WHERE b.booker.id = :bookerId
              AND b.status = 'REJECTED'
            ORDER BY b.start DESC
            """)
    List<Booking> findAllByBookerIdAndStateRejected(long bookerId);

    List<Booking> findAllByItemOwnerIdOrderByStartDesc(long ownerId);

    @Query("""
            SELECT b FROM Booking b
            WHERE b.item.owner.id = :ownerId
              AND b.end < CURRENT_TIMESTAMP
            ORDER BY b.start DESC
            """)
    List<Booking> findAllByItemOwnerIdAndStatePast(long ownerId);

    @Query("""
            SELECT b FROM Booking b
            WHERE b.item.owner.id = :ownerId
              AND b.start <= CURRENT_TIMESTAMP
              AND b.end >= CURRENT_TIMESTAMP
            ORDER BY b.start DESC
            """)
    List<Booking> findAllByItemOwnerIdAndStateCurrent(long ownerId);

    @Query("""
            SELECT b FROM Booking b
            WHERE b.item.owner.id = :ownerId
              AND b.start > CURRENT_TIMESTAMP
            ORDER BY b.start DESC
            """)
    List<Booking> findAllByItemOwnerIdAndStateFuture(long ownerId);

    @Query("""
            SELECT b FROM Booking b
            WHERE b.item.owner.id = :ownerId
              AND b.status = 'WAITING'
            ORDER BY b.start DESC
            """)
    List<Booking> findAllByItemOwnerIdAndStateWaiting(long ownerId);

    @Query("""
            SELECT b FROM Booking b
            WHERE b.item.owner.id = :ownerId
              AND b.status = 'REJECTED'
            ORDER BY b.start DESC
            """)
    List<Booking> findAllByItemOwnerIdAndStateRejected(long ownerId);
}
