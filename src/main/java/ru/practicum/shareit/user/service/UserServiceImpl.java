package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.common.exception.ConflictException;
import ru.practicum.shareit.common.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepo;

    @Override
    public UserDto create(CreateUserDto dto) {
        if (userRepo.existsByEmail(dto.getEmail())) {
            throw new ConflictException("Email уже существует: " + dto.getEmail());
        }
        User user = UserMapper.fromCreate(dto);
        return UserMapper.toDto(userRepo.save(user));
    }

    @Override
    public UserDto update(Long userId, CreateUserDto patch) {
        User existing = userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден: " + userId));

        String newEmail = patch.getEmail();
        userRepo.findByEmail(newEmail)
                .filter(u -> !u.getId().equals(userId))
                .ifPresent(u -> {
                    throw new ConflictException("Email уже существует: " + newEmail);
                });
        User newUser = UserMapper.fromUpdate(patch, existing);

        return UserMapper.toDto(userRepo.save(newUser));
    }

    @Override
    public UserDto getById(Long userId) {
        return userRepo.findById(userId)
                .map(UserMapper::toDto)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден: " + userId));
    }

    @Override
    public List<UserDto> getAll() {
        return UserMapper.toDto(userRepo.findAll());
    }

    @Override
    public void delete(Long userId) {
        if (!userRepo.existsById(userId)) {
            throw new NotFoundException("Пользователь не найден: " + userId);
        }
        userRepo.deleteById(userId);
    }
}
