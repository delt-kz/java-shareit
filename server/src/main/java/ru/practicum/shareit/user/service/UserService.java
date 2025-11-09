package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;

import java.util.List;



public interface UserService {
    UserResponseDto create(UserRequestDto dto);

    UserResponseDto update(Long userId, UserResponseDto patch);

    UserResponseDto getById(Long userId);

    List<UserResponseDto> getAll();

    void delete(Long userId);
}
