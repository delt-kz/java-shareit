package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.service.ItemService;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemService itemService;

    private ItemRequestDto requestDto;
    private ItemResponseDto responseDto;
    private ItemWithBookingDto itemWithBookingDto;
    private CommentDto commentDto;

    @BeforeEach
    void setUp() {
        requestDto = new ItemRequestDto("Drill", "Powerful drill", true, null);
        responseDto = new ItemResponseDto(1L, "Drill", "Powerful drill", true, 2L, null);
        itemWithBookingDto = new ItemWithBookingDto(1L, "Drill", "Powerful drill", true, 2L, null, null, null, List.of());
        commentDto = new CommentDto(5L, "Nice item", 1L, "John", LocalDateTime.now());
    }

    @Test
    void shouldCreateItem() throws Exception {
        when(itemService.create(any(), anyLong())).thenReturn(responseDto);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(responseDto.getId()))
                .andExpect(jsonPath("$.name").value(responseDto.getName()));

        ArgumentCaptor<ItemRequestDto> captor = ArgumentCaptor.forClass(ItemRequestDto.class);
        verify(itemService).create(captor.capture(), eq(2L));
        assertThat(captor.getValue()).usingRecursiveComparison().isEqualTo(requestDto);
    }

    @Test
    void shouldUpdateItem() throws Exception {
        when(itemService.update(any(), anyLong(), anyLong())).thenReturn(responseDto);

        mvc.perform(patch("/items/{itemId}", 1L)
                        .header("X-Sharer-User-Id", 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value(responseDto.getDescription()));

        ArgumentCaptor<ItemRequestDto> captor = ArgumentCaptor.forClass(ItemRequestDto.class);
        verify(itemService).update(captor.capture(), eq(2L), eq(1L));
        assertThat(captor.getValue()).usingRecursiveComparison().isEqualTo(requestDto);
    }

    @Test
    void shouldGetItemById() throws Exception {
        when(itemService.getById(1L, 3L)).thenReturn(itemWithBookingDto);

        mvc.perform(get("/items/{itemId}", 1L)
                        .header("X-Sharer-User-Id", 3L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemWithBookingDto.getId()));

        verify(itemService).getById(1L, 3L);
    }

    @Test
    void shouldGetOwnerItems() throws Exception {
        when(itemService.getItemsByOwner(2L)).thenReturn(List.of(itemWithBookingDto));

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemWithBookingDto.getId()));

        verify(itemService).getItemsByOwner(2L);
    }

    @Test
    void shouldSearchItems() throws Exception {
        when(itemService.search("drill")).thenReturn(List.of(responseDto));

        mvc.perform(get("/items/search")
                        .param("text", "drill"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value(responseDto.getName()));

        verify(itemService).search("drill");
    }

    @Test
    void shouldCreateComment() throws Exception {
        when(itemService.createComment(any(), anyLong(), anyLong())).thenReturn(commentDto);

        mvc.perform(post("/items/{itemId}/comment", 1L)
                        .header("X-Sharer-User-Id", 4L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commentDto.getId()));

        ArgumentCaptor<CommentDto> captor = ArgumentCaptor.forClass(CommentDto.class);
        verify(itemService).createComment(captor.capture(), eq(4L), eq(1L));
        assertThat(captor.getValue()).usingRecursiveComparison().isEqualTo(commentDto);
    }
}
