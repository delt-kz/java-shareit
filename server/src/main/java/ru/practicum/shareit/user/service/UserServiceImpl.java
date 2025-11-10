package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.common.exception.ConflictException;
import ru.practicum.shareit.common.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepo;


    @Override
    @Transactional
    public UserResponseDto create(UserRequestDto dto) {
        if (userRepo.existsByEmail(dto.getEmail())) {
            throw new ConflictException("Email уже существует: " + dto.getEmail());
        }
        User user = UserMapper.fromCreate(dto);
        return UserMapper.toDto(userRepo.save(user));
    }

    @Override
    @Transactional
    public UserResponseDto update(Long userId, UserRequestDto patch) {
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
    public UserResponseDto getById(Long userId) {
        return userRepo.findById(userId)
                .map(UserMapper::toDto)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден: " + userId));
    }

    @Override
    public List<UserResponseDto> getAll() {
        return UserMapper.toDto(userRepo.findAll());
    }

    @Override
    @Transactional
    public void delete(Long userId) {
        if (!userRepo.existsById(userId)) {
            throw new NotFoundException("Пользователь не найден: " + userId);
        }
        userRepo.deleteById(userId);
    }
}
