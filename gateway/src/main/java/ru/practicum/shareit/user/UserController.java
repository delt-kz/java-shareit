package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {

    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody @Valid CreateUserDto dto) {
        log.info("Creating user {}", dto);
        return userClient.create(dto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable Long id,
                                         @RequestBody UpdateUserDto patch) {
        log.info("Updating user id={}, patch={}", id, patch);
        return userClient.update(id, patch);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable Long id) {
        log.info("Getting user id={}", id);
        return userClient.getById(id);
    }

    @GetMapping
    public ResponseEntity<Object> getAll() {
        log.info("Getting all users");
        return userClient.getAll();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable Long id) {
        log.info("Deleting user id={}", id);
        return userClient.delete(id);
    }
}
