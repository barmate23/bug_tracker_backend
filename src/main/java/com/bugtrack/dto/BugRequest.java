package com.bugtrack.dto;

import com.bugtrack.model.BugStatus;
import com.bugtrack.model.Priority;

public record BugRequest(
    String title,
    String description,
    BugStatus status,
    Priority priority,
    Long assignedTo
) {}
