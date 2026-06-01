package com.bugtrack.service;

import com.bugtrack.model.Bug;
import com.bugtrack.model.Role;
import com.bugtrack.repository.UserProjectRepo;
import org.springframework.stereotype.Service;

@Service
public class AccessService {
  private final UserProjectRepo userProjectRepo;

  public AccessService(UserProjectRepo userProjectRepo) {
    this.userProjectRepo = userProjectRepo;
  }

  public boolean canAccessProject(SessionUser user, Long projectId) {
    return user.role() == Role.ADMIN || userProjectRepo.existsByUserIdAndProjectId(user.userId(), projectId);
  }

  public void requireProjectAccess(SessionUser user, Long projectId) {
    if (!canAccessProject(user, projectId)) {
      throw new ForbiddenException("You do not have access to this project");
    }
  }

  public void requireBugAccess(SessionUser user, Bug bug) {
    requireProjectAccess(user, bug.getProject().getId());
  }

  public void requireAdmin(SessionUser user) {
    if (user.role() != Role.ADMIN) {
      throw new ForbiddenException("Admin access required");
    }
  }
}
