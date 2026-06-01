package com.bugtrack.service;

import com.bugtrack.model.User;
import com.bugtrack.repository.UserRepo;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
  public static final String SESSION_USER = "AUTH_USER";

  private final UserRepo userRepo;

  public AuthService(UserRepo userRepo) {
    this.userRepo = userRepo;
  }

  public SessionUser login(String username, String password, HttpSession session) {
    User user = userRepo.findByUsername(username)
        .filter(found -> found.getPassword().equals(password))
        .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));
    SessionUser sessionUser = new SessionUser(user.getId(), user.getUsername(), user.getFullName(), user.getRole());
    session.setAttribute(SESSION_USER, sessionUser);
    return sessionUser;
  }

  public SessionUser current(HttpSession session) {
    if (session == null) {
      return null;
    }
    Object value = session.getAttribute(SESSION_USER);
    return value instanceof SessionUser user ? user : null;
  }
}
