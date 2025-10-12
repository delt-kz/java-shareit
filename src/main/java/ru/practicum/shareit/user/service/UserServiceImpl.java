package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.common.exception.ConflictException;
import ru.practicum.shareit.common.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserStorage;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage repository;

    @Override
    public UserDto create(UserDto dto) {
        if (repository.existsByEmail(dto.getEmail())) {
            throw new ConflictException("Email уже существует: " + dto.getEmail());
        }
        User user = UserMapper.mapToUser(dto);
        return UserMapper.mapToUserDto(repository.save(user));
    }

    @Override
    public UserDto update(Long userId, UserDto patch) {
        User existing = repository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден: " + userId));

        String newEmail = patch.getEmail();
        repository.findByEmail(newEmail)
                .filter(u -> !u.getId().equals(userId))
                .ifPresent(u -> {
                    throw new ConflictException("Email уже существует: " + newEmail);
                });
        UserMapper.mapFromUpdate(patch, existing);

        return UserMapper.mapToUserDto(repository.save(existing));
    }

    @Override
    public UserDto getById(Long userId) {
        return repository.findById(userId)
                .map(UserMapper::mapToUserDto)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден: " + userId));
    }

    @Override
    public List<UserDto> getAll() {
        return repository.findAll().stream().map(UserMapper::mapToUserDto).collect(Collectors.toList());
    }

    @Override
    public void delete(Long userId) {
        if (!repository.existsById(userId)) {
            throw new NotFoundException("Пользователь не найден: " + userId);
        }
        repository.deleteById(userId);
    }
}
