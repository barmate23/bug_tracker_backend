package com.bugtrack.service;

import com.bugtrack.model.Role;

public record SessionUser(Long userId, String username, String fullName, Role role) {}
