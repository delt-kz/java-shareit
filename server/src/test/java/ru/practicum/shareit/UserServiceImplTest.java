package ru.practicum.shareit;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceImplTest {

    private final EntityManager em;
    private final UserService service;

    @Test
    void saveNewUser() throws Exception {
        UserRequestDto userDto = new UserRequestDto("John", "gmail@gmail.com");

        service.create(userDto);

        TypedQuery<User> query = em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class);
        User user = query.setParameter("email", userDto.getEmail()).getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    void updateUser() throws Exception {
        UserRequestDto userDto = new UserRequestDto("John", "gmail@gmail.com");
        service.create(userDto);
        TypedQuery<User> query = em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class);
        User user = query.setParameter("email", userDto.getEmail()).getSingleResult();

        UserRequestDto userDto1 = new UserRequestDto("Doe", "gmail@mail.com");
        service.update(user.getId(), userDto1);
        TypedQuery<User> query1 = em.createQuery("SELECT u FROM User u WHERE u.id = :id", User.class);
        User user1 = query1.setParameter("id", user.getId()).getSingleResult();

        assertThat(user1.getId(), notNullValue());
        assertThat(user1.getName(), equalTo(userDto1.getName()));
        assertThat(user1.getEmail(), equalTo(userDto1.getEmail()));
    }

    @Test
    void getUserById() throws Exception {
        UserRequestDto userDto = new UserRequestDto("John", "gmail@gmail.com");

        service.create(userDto);

        TypedQuery<User> query = em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class);
        User user = query.setParameter("email", userDto.getEmail()).getSingleResult();

        UserResponseDto user1 = service.getById(user.getId());
        assertThat(user1.getId(), notNullValue());
        assertThat(user1.getName(), equalTo(userDto.getName()));
        assertThat(user1.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    void getAllUsers() {
        UserRequestDto userDto = new UserRequestDto("John", "gmail@gmail.com");
        UserRequestDto userDto1 = new UserRequestDto("Mike", "mike@gmail.com");

        service.create(userDto);
        service.create(userDto1);

        List<UserResponseDto> users = service.getAll();

        assertThat(users, hasSize(2));
        assertTrue(users.stream()
                .anyMatch(u -> u.getName().equals("John") && u.getEmail().equals("gmail@gmail.com")));
        assertTrue(users.stream()
                .anyMatch(u -> u.getName().equals("Mike") && u.getEmail().equals("mike@gmail.com")));

    }

    @Test
    void deleteUser() {
        UserRequestDto userDto = new UserRequestDto("John", "gmail@gmail.com");

        service.create(userDto);

        TypedQuery<User> query = em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class);
        User user = query.setParameter("email", userDto.getEmail()).getSingleResult();

        service.delete(user.getId());

        assertThrows(NoResultException.class, () -> {
            em.createQuery("SELECT u FROM User u WHERE u.id = :id", User.class)
                    .setParameter("id", user.getId())
                    .getSingleResult();
        });
    }
}
