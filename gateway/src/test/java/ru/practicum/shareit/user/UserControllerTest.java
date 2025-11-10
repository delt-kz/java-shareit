package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private UserClient userClient;

    @Test
    void shouldValidateCreateUserRequest() throws Exception {
        CreateUserDto invalid = new CreateUserDto(null, "", "not-email");

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldForwardCreateUserToClient() throws Exception {
        CreateUserDto dto = new CreateUserDto(null, "John", "john@example.com");
        when(userClient.create(any())).thenReturn(ResponseEntity.ok().build());

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        ArgumentCaptor<CreateUserDto> captor = ArgumentCaptor.forClass(CreateUserDto.class);
        verify(userClient).create(captor.capture());
        assertThat(captor.getValue()).usingRecursiveComparison().isEqualTo(dto);
    }

    @Test
    void shouldForwardUpdateUserToClient() throws Exception {
        UpdateUserDto patch = new UpdateUserDto("new@example.com", "New name");
        when(userClient.update(eq(1L), any())).thenReturn(ResponseEntity.ok().build());

        mvc.perform(patch("/users/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(patch)))
                .andExpect(status().isOk());

        ArgumentCaptor<UpdateUserDto> captor = ArgumentCaptor.forClass(UpdateUserDto.class);
        verify(userClient).update(eq(1L), captor.capture());
        assertThat(captor.getValue()).usingRecursiveComparison().isEqualTo(patch);
    }

    @Test
    void shouldForwardGetUserToClient() throws Exception {
        when(userClient.getById(1L)).thenReturn(ResponseEntity.ok().build());

        mvc.perform(get("/users/{id}", 1L))
                .andExpect(status().isOk());

        verify(userClient).getById(1L);
    }

    @Test
    void shouldForwardGetAllUsersToClient() throws Exception {
        when(userClient.getAll()).thenReturn(ResponseEntity.ok().build());

        mvc.perform(get("/users"))
                .andExpect(status().isOk());

        verify(userClient).getAll();
    }

    @Test
    void shouldForwardDeleteUserToClient() throws Exception {
        when(userClient.delete(1L)).thenReturn(ResponseEntity.ok().build());

        mvc.perform(delete("/users/{id}", 1L))
                .andExpect(status().isOk());

        verify(userClient).delete(1L);
    }
}
