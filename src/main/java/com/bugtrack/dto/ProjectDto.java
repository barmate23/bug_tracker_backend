package com.bugtrack.dto;

import com.bugtrack.model.Project;
import java.time.Instant;

public record ProjectDto(Long id, String name, String description, Instant createdAt, long bugCount) {
  public static ProjectDto from(Project project, long bugCount) {
    return new ProjectDto(project.getId(), project.getName(), project.getDescription(), project.getCreatedAt(), bugCount);
  }
}
