package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateItemDto {
    @NotNull
    private String name;
    @NotNull
    private String description;
    @NotNull
    private Boolean available;
    private Long requestId;
}
