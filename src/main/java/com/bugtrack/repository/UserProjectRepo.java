package com.bugtrack.repository;

import com.bugtrack.model.Project;
import com.bugtrack.model.User;
import com.bugtrack.model.UserProject;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProjectRepo extends JpaRepository<UserProject, Long> {
  boolean existsByUserIdAndProjectId(Long userId, Long projectId);
  List<UserProject> findByUserId(Long userId);
  List<UserProject> findByProjectId(Long projectId);
  void deleteByUserIdAndProjectId(Long userId, Long projectId);
  boolean existsByUserAndProject(User user, Project project);
}
