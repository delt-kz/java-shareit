package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.dto.ItemRequestWithAnswerResponseDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemRequestService itemRequestService;

    private ItemRequestRequestDto requestDto;
    private ItemRequestResponseDto responseDto;
    private ItemRequestWithAnswerResponseDto withAnswersDto;

    @BeforeEach
    void setUp() {
        requestDto = new ItemRequestRequestDto("Need a drill", LocalDateTime.now());
        responseDto = new ItemRequestResponseDto(5L, "Need a drill", 1L, LocalDateTime.now());
        withAnswersDto = new ItemRequestWithAnswerResponseDto(5L, "Need a drill", 1L, LocalDateTime.now(), List.of());
    }

    @Test
    void shouldCreateRequest() throws Exception {
        when(itemRequestService.create(any(), eq(2L))).thenReturn(responseDto);

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(responseDto.getId()));

        ArgumentCaptor<ItemRequestRequestDto> captor = ArgumentCaptor.forClass(ItemRequestRequestDto.class);
        verify(itemRequestService).create(captor.capture(), eq(2L));
        assertThat(captor.getValue()).usingRecursiveComparison().isEqualTo(requestDto);
    }

    @Test
    void shouldGetUserRequests() throws Exception {
        when(itemRequestService.getUserRequests(1L)).thenReturn(List.of(withAnswersDto));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(withAnswersDto.getId()));

        verify(itemRequestService).getUserRequests(1L);
    }

    @Test
    void shouldGetAllRequests() throws Exception {
        when(itemRequestService.getAll(2L)).thenReturn(List.of(responseDto));

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(responseDto.getId()));

        verify(itemRequestService).getAll(2L);
    }

    @Test
    void shouldGetRequestById() throws Exception {
        when(itemRequestService.getById(5L, 3L)).thenReturn(withAnswersDto);

        mvc.perform(get("/requests/{requestId}", 5L)
                        .header("X-Sharer-User-Id", 3L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(withAnswersDto.getId()));

        verify(itemRequestService).getById(5L, 3L);
    }
}
