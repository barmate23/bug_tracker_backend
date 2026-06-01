package com.bugtrack.controller;

import com.bugtrack.dto.AdminUserDto;
import com.bugtrack.dto.ApiResponse;
import com.bugtrack.dto.UserDto;
import com.bugtrack.dto.UserRequest;
import com.bugtrack.model.Role;
import com.bugtrack.model.User;
import com.bugtrack.repository.UserRepo;
import com.bugtrack.service.AccessService;
import com.bugtrack.service.AuthService;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {
  private final UserRepo userRepo;
  private final AuthService authService;
  private final AccessService accessService;

  public UserController(UserRepo userRepo, AuthService authService, AccessService accessService) {
    this.userRepo = userRepo;
    this.authService = authService;
    this.accessService = accessService;
  }

  @GetMapping
  public ApiResponse<?> list(HttpSession session) {
    if (authService.current(session).role() == Role.ADMIN) {
      return ApiResponse.ok(userRepo.findAll().stream().map(AdminUserDto::from).toList());
    }
    return ApiResponse.ok(userRepo.findAll().stream().map(UserDto::from).toList());
  }

  @PostMapping
  public ApiResponse<UserDto> create(@RequestBody UserRequest request, HttpSession session) {
    accessService.requireAdmin(authService.current(session));
    User user = userRepo.save(new User(request.username(), request.password(), request.fullName(), request.role()));
    return ApiResponse.ok(UserDto.from(user), "User created");
  }
}
