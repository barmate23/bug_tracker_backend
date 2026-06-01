package com.bugtrack.controller;

import com.bugtrack.dto.ApiResponse;
import com.bugtrack.dto.LoginRequest;
import com.bugtrack.service.AuthService;
import com.bugtrack.service.SessionUser;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/login")
  public ApiResponse<SessionUser> login(@RequestBody LoginRequest request, HttpSession session) {
    return ApiResponse.ok(authService.login(request.username(), request.password(), session));
  }

  @PostMapping("/logout")
  public ApiResponse<Void> logout(HttpSession session) {
    session.invalidate();
    return ApiResponse.ok(null, "Logged out");
  }

  @GetMapping("/me")
  public ResponseEntity<ApiResponse<SessionUser>> me(HttpSession session) {
    SessionUser user = authService.current(session);
    if (user == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("Not authenticated"));
    }
    return ResponseEntity.ok(ApiResponse.ok(user));
  }
}
