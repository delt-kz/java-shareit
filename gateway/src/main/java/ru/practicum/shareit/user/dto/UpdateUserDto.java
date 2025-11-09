// DTO только для PATCH
package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserDto {

    @Email(message = "Некорректный email")
    @Size(max = 255, message = "Email слишком длинный")
    private String email;

    @Size(max = 255, message = "Имя слишком длинное")
    private String name;
}
