package com.bugtrack.dto;

import com.bugtrack.model.Bug;
import com.bugtrack.model.BugStatus;
import com.bugtrack.model.Priority;
import java.time.Instant;

public record BugDto(
    Long id,
    Long projectId,
    String projectName,
    String title,
    String description,
    BugStatus status,
    Priority priority,
    UserDto createdBy,
    UserDto assignedTo,
    Instant createdAt,
    Instant updatedAt
) {
  public static BugDto from(Bug bug) {
    return new BugDto(
        bug.getId(),
        bug.getProject().getId(),
        bug.getProject().getName(),
        bug.getTitle(),
        bug.getDescription(),
        bug.getStatus(),
        bug.getPriority(),
        UserDto.from(bug.getCreatedBy()),
        bug.getAssignedTo() == null ? null : UserDto.from(bug.getAssignedTo()),
        bug.getCreatedAt(),
        bug.getUpdatedAt()
    );
  }
}
