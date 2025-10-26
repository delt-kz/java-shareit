package ru.practicum.shareit.item.dto;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private Long id;
    @NotNull
    private String text;
    private Long itemId;
    private String authorName;
    private LocalDateTime created;
}
