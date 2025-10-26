package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long owner;
    private Long requestId;
    private List<CommentDto> comments;

    public ItemDto(Long id, String name, String description, Boolean available, Long owner, Long requestId) {
        this.id = id;
        this.requestId = requestId;
        this.owner = owner;
        this.available = available;
        this.description = description;
        this.name = name;
    }
}