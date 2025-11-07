package ru.practicum.shareit.user;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {

    public static UserDto toDto(User user) {
        return new UserDto(user.getId(),
                user.getName(),
                user.getEmail());
    }

    public static User fromUpdate(UserDto patch, User oldUser) {
        User newUser = new User();
        if (patch.getEmail() != null) {
            newUser.setEmail(patch.getEmail());
        } else {
            newUser.setEmail(oldUser.getEmail());
        }
        if (patch.getName() != null) {
            newUser.setName(patch.getName());
        } else {
            newUser.setName(oldUser.getName());
        }
        newUser.setId(oldUser.getId());
        return newUser;
    }

    public static User fromCreate(CreateUserDto dto) {
        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        return user;
    }

    public static List<UserDto> toDto(List<User> users) {
        List<UserDto> result = new ArrayList<>();

        for (User user : users) {
            result.add(toDto(user));
        }

        return result;
    }
}
