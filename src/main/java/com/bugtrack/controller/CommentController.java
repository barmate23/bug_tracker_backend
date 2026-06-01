package com.bugtrack.controller;

import com.bugtrack.dto.ApiResponse;
import com.bugtrack.dto.CommentDto;
import com.bugtrack.dto.CommentRequest;
import com.bugtrack.model.Bug;
import com.bugtrack.model.Comment;
import com.bugtrack.model.User;
import com.bugtrack.repository.BugRepo;
import com.bugtrack.repository.CommentRepo;
import com.bugtrack.repository.UserRepo;
import com.bugtrack.service.AccessService;
import com.bugtrack.service.AuthService;
import com.bugtrack.service.SessionUser;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bugs/{bugId}/comments")
public class CommentController {
  private final CommentRepo commentRepo;
  private final BugRepo bugRepo;
  private final UserRepo userRepo;
  private final AuthService authService;
  private final AccessService accessService;

  public CommentController(CommentRepo commentRepo, BugRepo bugRepo, UserRepo userRepo, AuthService authService,
      AccessService accessService) {
    this.commentRepo = commentRepo;
    this.bugRepo = bugRepo;
    this.userRepo = userRepo;
    this.authService = authService;
    this.accessService = accessService;
  }

  @GetMapping
  public ApiResponse<List<CommentDto>> list(@PathVariable Long bugId, HttpSession session) {
    Bug bug = loadAccessibleBug(bugId, authService.current(session));
    return ApiResponse.ok(commentRepo.findByBugIdOrderByCreatedAtAsc(bug.getId()).stream().map(CommentDto::from).toList());
  }

  @PostMapping
  public ApiResponse<CommentDto> create(@PathVariable Long bugId, @RequestBody CommentRequest request, HttpSession session) {
    SessionUser current = authService.current(session);
    Bug bug = loadAccessibleBug(bugId, current);
    User user = userRepo.findById(current.userId()).orElseThrow(() -> new EntityNotFoundException("User not found"));
    Comment comment = commentRepo.save(new Comment(bug, user, request.message()));
    return ApiResponse.ok(CommentDto.from(comment), "Comment added");
  }

  private Bug loadAccessibleBug(Long bugId, SessionUser current) {
    Bug bug = bugRepo.findById(bugId).orElseThrow(() -> new EntityNotFoundException("Bug not found"));
    accessService.requireBugAccess(current, bug);
    return bug;
  }
}
