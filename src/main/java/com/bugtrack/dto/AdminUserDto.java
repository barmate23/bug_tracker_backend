package com.bugtrack.dto;

import com.bugtrack.model.Role;
import com.bugtrack.model.User;

public record AdminUserDto(Long userId, String username, String fullName, Role role, String password) {
  public static AdminUserDto from(User user) {
    return new AdminUserDto(user.getId(), user.getUsername(), user.getFullName(), user.getRole(), user.getPassword());
  }
}
