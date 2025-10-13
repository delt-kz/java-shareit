package ru.practicum.shareit.booking;

import lombok.Data;

import java.util.Date;

/**
 * TODO Sprint add-bookings.
 */

@Data
public class Booking {
    private Long id;
    private Long ownerId;
    private Long userId;
    private Date startDate;
    private Date endDate;
    private Boolean isConfirmed;
}
