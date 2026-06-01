package com.bugtrack.dto;

import com.bugtrack.model.Role;

public record UserRequest(String username, String password, String fullName, Role role) {}
