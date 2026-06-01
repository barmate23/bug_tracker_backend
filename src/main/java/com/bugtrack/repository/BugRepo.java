package com.bugtrack.repository;

import com.bugtrack.model.Bug;
import com.bugtrack.model.BugStatus;
import com.bugtrack.model.Priority;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BugRepo extends JpaRepository<Bug, Long> {
  List<Bug> findByProjectIdOrderByCreatedAtDesc(Long projectId);
  long countByProjectId(Long projectId);
  List<Bug> findByProjectIdAndStatusOrderByCreatedAtDesc(Long projectId, BugStatus status);
  List<Bug> findByProjectIdAndPriorityOrderByCreatedAtDesc(Long projectId, Priority priority);
  List<Bug> findByProjectIdAndAssignedToIdOrderByCreatedAtDesc(Long projectId, Long assignedToId);
}
