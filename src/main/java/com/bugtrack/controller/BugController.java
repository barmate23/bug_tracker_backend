package com.bugtrack.controller;

import com.bugtrack.dto.ApiResponse;
import com.bugtrack.dto.BugDto;
import com.bugtrack.dto.BugRequest;
import com.bugtrack.model.Bug;
import com.bugtrack.model.BugStatus;
import com.bugtrack.model.Priority;
import com.bugtrack.model.Project;
import com.bugtrack.model.User;
import com.bugtrack.repository.BugAttachmentRepo;
import com.bugtrack.repository.BugRepo;
import com.bugtrack.repository.CommentRepo;
import com.bugtrack.repository.ProjectRepo;
import com.bugtrack.repository.UserRepo;
import com.bugtrack.service.AccessService;
import com.bugtrack.service.AuthService;
import com.bugtrack.service.SessionUser;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class BugController {
  private final BugRepo bugRepo;
  private final BugAttachmentRepo attachmentRepo;
  private final CommentRepo commentRepo;
  private final ProjectRepo projectRepo;
  private final UserRepo userRepo;
  private final AuthService authService;
  private final AccessService accessService;

  public BugController(BugRepo bugRepo, BugAttachmentRepo attachmentRepo, CommentRepo commentRepo,
      ProjectRepo projectRepo, UserRepo userRepo, AuthService authService, AccessService accessService) {
    this.bugRepo = bugRepo;
    this.attachmentRepo = attachmentRepo;
    this.commentRepo = commentRepo;
    this.projectRepo = projectRepo;
    this.userRepo = userRepo;
    this.authService = authService;
    this.accessService = accessService;
  }

  @GetMapping("/projects/{projectId}/bugs")
  public ApiResponse<List<BugDto>> list(@PathVariable Long projectId,
      @RequestParam(required = false) BugStatus status,
      @RequestParam(required = false) Priority priority,
      @RequestParam(required = false) Long assignee,
      HttpSession session) {
    SessionUser current = authService.current(session);
    accessService.requireProjectAccess(current, projectId);
    List<Bug> bugs = bugRepo.findByProjectIdOrderByCreatedAtDesc(projectId);
    return ApiResponse.ok(bugs.stream()
        .filter(bug -> status == null || bug.getStatus() == status)
        .filter(bug -> priority == null || bug.getPriority() == priority)
        .filter(bug -> assignee == null || (bug.getAssignedTo() != null && bug.getAssignedTo().getId().equals(assignee)))
        .map(BugDto::from)
        .toList());
  }

  @PostMapping("/projects/{projectId}/bugs")
  public ApiResponse<BugDto> create(@PathVariable Long projectId, @RequestBody BugRequest request, HttpSession session) {
    SessionUser current = authService.current(session);
    accessService.requireProjectAccess(current, projectId);
    Project project = projectRepo.findById(projectId).orElseThrow(() -> new EntityNotFoundException("Project not found"));
    User creator = userRepo.findById(current.userId()).orElseThrow(() -> new EntityNotFoundException("User not found"));
    Bug bug = new Bug();
    applyRequest(bug, request);
    bug.setStatus(request.status() == null ? BugStatus.OPEN : request.status());
    bug.setProject(project);
    bug.setCreatedBy(creator);
    return ApiResponse.ok(BugDto.from(bugRepo.save(bug)), "Bug created");
  }

  @GetMapping("/bugs/{bugId}")
  public ApiResponse<BugDto> get(@PathVariable Long bugId, HttpSession session) {
    Bug bug = loadAccessibleBug(bugId, authService.current(session));
    return ApiResponse.ok(BugDto.from(bug));
  }

  @PutMapping("/bugs/{bugId}")
  public ApiResponse<BugDto> update(@PathVariable Long bugId, @RequestBody BugRequest request, HttpSession session) {
    SessionUser current = authService.current(session);
    Bug bug = loadAccessibleBug(bugId, current);
    applyRequest(bug, request);
    bug.setUpdatedAt(Instant.now());
    return ApiResponse.ok(BugDto.from(bugRepo.save(bug)), "Bug updated");
  }

  @DeleteMapping("/bugs/{bugId}")
  @Transactional
  public ApiResponse<Void> delete(@PathVariable Long bugId, HttpSession session) {
    Bug bug = loadAccessibleBug(bugId, authService.current(session));
    attachmentRepo.deleteByBugId(bug.getId());
    commentRepo.deleteByBugId(bug.getId());
    bugRepo.delete(bug);
    return ApiResponse.ok(null, "Bug deleted");
  }

  private Bug loadAccessibleBug(Long bugId, SessionUser current) {
    Bug bug = bugRepo.findById(bugId).orElseThrow(() -> new EntityNotFoundException("Bug not found"));
    accessService.requireBugAccess(current, bug);
    return bug;
  }

  private void applyRequest(Bug bug, BugRequest request) {
    if (request.title() != null) {
      bug.setTitle(request.title());
    }
    if (request.description() != null) {
      bug.setDescription(request.description());
    }
    if (request.status() != null) {
      bug.setStatus(request.status());
    }
    if (request.priority() != null) {
      bug.setPriority(request.priority());
    }
    bug.setAssignedTo(request.assignedTo() == null ? null : userRepo.findById(request.assignedTo())
        .orElseThrow(() -> new EntityNotFoundException("Assignee not found")));
  }
}
