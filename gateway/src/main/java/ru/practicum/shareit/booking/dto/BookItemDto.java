package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookItemDto {
	private long itemId;
	private LocalDateTime start;
	@Future
	private LocalDateTime end;
}
