package com.bugtrack.dto;

import com.bugtrack.model.Comment;
import java.time.Instant;

public record CommentDto(Long id, Long bugId, UserDto user, String message, Instant createdAt) {
  public static CommentDto from(Comment comment) {
    return new CommentDto(
        comment.getId(),
        comment.getBug().getId(),
        UserDto.from(comment.getUser()),
        comment.getMessage(),
        comment.getCreatedAt()
    );
  }
}
