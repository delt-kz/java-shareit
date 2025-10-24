package ru.practicum.shareit.user.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;


@Transactional(readOnly = true)
public interface UserService {
    @Transactional
    UserDto create(CreateUserDto dto);

    @Transactional
    UserDto update(Long userId, CreateUserDto patch);

    UserDto getById(Long userId);

    List<UserDto> getAll();

    @Transactional
    void delete(Long userId);
}
