package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.CreateItemDto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemClient itemClient;

    @Test
    void shouldValidateCreateItemBody() throws Exception {
        CreateItemDto invalidDto = new CreateItemDto(null, "Description", true, null);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldForwardCreateItemToClient() throws Exception {
        CreateItemDto dto = new CreateItemDto("Drill", "Description", true, null);
        when(itemClient.create(eq(1L), any())).thenReturn(ResponseEntity.ok().build());

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        ArgumentCaptor<CreateItemDto> captor = ArgumentCaptor.forClass(CreateItemDto.class);
        verify(itemClient).create(eq(1L), captor.capture());
        assertThat(captor.getValue()).usingRecursiveComparison().isEqualTo(dto);
    }

    @Test
    void shouldForwardUpdateItemToClient() throws Exception {
        CreateItemDto patchDto = new CreateItemDto("New name", null, null, null);
        when(itemClient.update(eq(1L), eq(2L), any())).thenReturn(ResponseEntity.ok().build());

        mvc.perform(patch("/items/{itemId}", 2L)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(patchDto)))
                .andExpect(status().isOk());

        ArgumentCaptor<CreateItemDto> captor = ArgumentCaptor.forClass(CreateItemDto.class);
        verify(itemClient).update(eq(1L), eq(2L), captor.capture());
        assertThat(captor.getValue()).usingRecursiveComparison().isEqualTo(patchDto);
    }

    @Test
    void shouldForwardGetItemToClient() throws Exception {
        when(itemClient.getById(2L, 3L)).thenReturn(ResponseEntity.ok().build());

        mvc.perform(get("/items/{itemId}", 2L)
                        .header("X-Sharer-User-Id", 3L))
                .andExpect(status().isOk());

        verify(itemClient).getById(2L, 3L);
    }

    @Test
    void shouldForwardOwnerItemsToClient() throws Exception {
        when(itemClient.getOwnerItems(4L)).thenReturn(ResponseEntity.ok().build());

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 4L))
                .andExpect(status().isOk());

        verify(itemClient).getOwnerItems(4L);
    }

    @Test
    void shouldForwardSearchToClient() throws Exception {
        when(itemClient.search("drill")).thenReturn(ResponseEntity.ok().build());

        mvc.perform(get("/items/search")
                        .param("text", "drill"))
                .andExpect(status().isOk());

        verify(itemClient).search("drill");
    }

    @Test
    void shouldValidateCommentBody() throws Exception {
        CreateCommentDto invalid = new CreateCommentDto("");

        mvc.perform(post("/items/{itemId}/comment", 1L)
                        .header("X-Sharer-User-Id", 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldForwardCommentToClient() throws Exception {
        CreateCommentDto dto = new CreateCommentDto("Nice item");
        when(itemClient.createComment(eq(2L), eq(1L), any())).thenReturn(ResponseEntity.ok().build());

        mvc.perform(post("/items/{itemId}/comment", 1L)
                        .header("X-Sharer-User-Id", 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        ArgumentCaptor<CreateCommentDto> captor = ArgumentCaptor.forClass(CreateCommentDto.class);
        verify(itemClient).createComment(eq(2L), eq(1L), captor.capture());
        assertThat(captor.getValue()).usingRecursiveComparison().isEqualTo(dto);
    }
}
