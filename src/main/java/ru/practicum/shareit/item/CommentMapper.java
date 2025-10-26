package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentMapper {

    public static Comment fromDto(CommentDto dto, Long userId, Long itemId) {
        Comment comment = new Comment();
        comment.setText(dto.getText());
        User user = new User();
        user.setId(userId);
        comment.setAuthor(user);
        Item item = new Item();
        item.setId(itemId);
        comment.setItem(item);
        return comment;
    }

    public static CommentDto toDto(Comment comment) {
        return new CommentDto(comment.getId(),
                comment.getText(),
                comment.getItem().getId(),
                comment.getAuthor().getName(),
                LocalDateTime.ofInstant(comment.getCreated(), ZoneOffset.UTC));
    }

    public static List<CommentDto> toDto(List<Comment> comments) {
        List<CommentDto> result = new ArrayList<>();

        for (Comment comment : comments) {
            result.add(toDto(comment));
        }

        return result;
    }
}
