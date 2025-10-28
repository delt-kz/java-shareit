package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;



public interface UserService {
    UserDto create(CreateUserDto dto);

    UserDto update(Long userId, UserDto patch);

    UserDto getById(Long userId);

    List<UserDto> getAll();

    void delete(Long userId);
}
