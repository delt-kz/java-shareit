package ru.practicum.shareit;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import ru.practicum.shareit.booking.BookingController;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {
}
