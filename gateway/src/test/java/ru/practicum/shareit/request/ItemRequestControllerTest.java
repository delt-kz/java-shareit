package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private RequestClient requestClient;

    @Test
    void shouldValidateCreateRequestBody() throws Exception {
        CreateItemRequestDto invalid = new CreateItemRequestDto(null);

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldForwardCreateRequestToClient() throws Exception {
        CreateItemRequestDto dto = new CreateItemRequestDto("Need a drill");
        when(requestClient.create(eq(1L), any())).thenReturn(ResponseEntity.ok().build());

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        ArgumentCaptor<CreateItemRequestDto> captor = ArgumentCaptor.forClass(CreateItemRequestDto.class);
        verify(requestClient).create(eq(1L), captor.capture());
        assertThat(captor.getValue()).usingRecursiveComparison().isEqualTo(dto);
    }

    @Test
    void shouldForwardGetByIdToClient() throws Exception {
        when(requestClient.getById(1L, 2L)).thenReturn(ResponseEntity.ok().build());

        mvc.perform(get("/requests/{requestId}", 2L)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());

        verify(requestClient).getById(1L, 2L);
    }

    @Test
    void shouldForwardUserRequestsToClient() throws Exception {
        when(requestClient.getUserRequests(1L)).thenReturn(ResponseEntity.ok().build());

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());

        verify(requestClient).getUserRequests(1L);
    }

    @Test
    void shouldForwardAllRequestsToClient() throws Exception {
        when(requestClient.getAll(1L)).thenReturn(ResponseEntity.ok().build());

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());

        verify(requestClient).getAll(1L);
    }
}
