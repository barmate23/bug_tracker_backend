package com.bugtrack.repository;

import com.bugtrack.model.Comment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepo extends JpaRepository<Comment, Long> {
  List<Comment> findByBugIdOrderByCreatedAtAsc(Long bugId);
}
