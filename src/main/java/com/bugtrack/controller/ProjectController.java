package com.bugtrack.controller;

import com.bugtrack.dto.ApiResponse;
import com.bugtrack.dto.AssignUserRequest;
import com.bugtrack.dto.ProjectDto;
import com.bugtrack.dto.ProjectRequest;
import com.bugtrack.dto.UserDto;
import com.bugtrack.model.Project;
import com.bugtrack.model.Role;
import com.bugtrack.model.User;
import com.bugtrack.model.UserProject;
import com.bugtrack.repository.BugRepo;
import com.bugtrack.repository.ProjectRepo;
import com.bugtrack.repository.UserProjectRepo;
import com.bugtrack.repository.UserRepo;
import com.bugtrack.service.AccessService;
import com.bugtrack.service.AuthService;
import com.bugtrack.service.SessionUser;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {
  private final ProjectRepo projectRepo;
  private final UserRepo userRepo;
  private final UserProjectRepo userProjectRepo;
  private final BugRepo bugRepo;
  private final AuthService authService;
  private final AccessService accessService;

  public ProjectController(ProjectRepo projectRepo, UserRepo userRepo, UserProjectRepo userProjectRepo, BugRepo bugRepo,
      AuthService authService, AccessService accessService) {
    this.projectRepo = projectRepo;
    this.userRepo = userRepo;
    this.userProjectRepo = userProjectRepo;
    this.bugRepo = bugRepo;
    this.authService = authService;
    this.accessService = accessService;
  }

  @GetMapping
  public ApiResponse<List<ProjectDto>> list(HttpSession session) {
    SessionUser user = authService.current(session);
    List<Project> projects = user.role() == Role.ADMIN
        ? projectRepo.findAll()
        : userProjectRepo.findByUserId(user.userId()).stream().map(UserProject::getProject).toList();
    return ApiResponse.ok(projects.stream()
        .map(project -> ProjectDto.from(project, bugRepo.countByProjectId(project.getId())))
        .toList());
  }

  @PostMapping
  public ApiResponse<ProjectDto> create(@RequestBody ProjectRequest request, HttpSession session) {
    accessService.requireAdmin(authService.current(session));
    Project project = projectRepo.save(new Project(request.name(), request.description()));
    return ApiResponse.ok(ProjectDto.from(project, 0), "Project created");
  }

  @GetMapping("/{id}/users")
  public ApiResponse<List<UserDto>> projectUsers(@PathVariable Long id, HttpSession session) {
    accessService.requireAdmin(authService.current(session));
    return ApiResponse.ok(userProjectRepo.findByProjectId(id).stream()
        .map(UserProject::getUser)
        .map(UserDto::from)
        .toList());
  }

  @PostMapping("/{id}/users")
  public ApiResponse<List<UserDto>> assignUser(@PathVariable Long id, @RequestBody AssignUserRequest request,
      HttpSession session) {
    accessService.requireAdmin(authService.current(session));
    Project project = projectRepo.findById(id).orElseThrow(() -> new EntityNotFoundException("Project not found"));
    User user = userRepo.findById(request.userId()).orElseThrow(() -> new EntityNotFoundException("User not found"));
    if (!userProjectRepo.existsByUserAndProject(user, project)) {
      userProjectRepo.save(new UserProject(user, project));
    }
    return projectUsers(id, session);
  }

  @DeleteMapping("/{projectId}/users/{userId}")
  @Transactional
  public ApiResponse<Void> removeUser(@PathVariable Long projectId, @PathVariable Long userId, HttpSession session) {
    accessService.requireAdmin(authService.current(session));
    userProjectRepo.deleteByUserIdAndProjectId(userId, projectId);
    return ApiResponse.ok(null, "User removed from project");
  }
}
