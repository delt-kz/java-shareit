package ru.practicum.shareit.booking;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.user.User;

import java.time.Instant;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "booking")
@Getter @Setter @ToString
public class Booking {
    @Id
    private Long id;
    @ManyToOne
    @JoinColumn(name = "booker_id")
    private User booker;
    private Instant start;
    private Instant end;
    @Enumerated(EnumType.STRING)
    private BookingStatus status;


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Booking booking = (Booking) o;
        return Objects.equals(id, booking.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
