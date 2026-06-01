package com.bugtrack.dto;

import com.bugtrack.model.Role;
import com.bugtrack.model.User;

public record UserDto(Long userId, String username, String fullName, Role role) {
  public static UserDto from(User user) {
    return new UserDto(user.getId(), user.getUsername(), user.getFullName(), user.getRole());
  }
}
