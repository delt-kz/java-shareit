package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("""
            UPDATE Booking b SET b.status = :status WHERE b.status = :id
            """)
    int updateStatus(@Param("status") BookingStatus status, @Param("id") long id);

    //
//    Booking findAllByBookerIdAndStartBeforeAndEndBefore(long bookerId, Instant now);
//
//    Booking findAllByBookerIdAndStatusIsNew();
}
