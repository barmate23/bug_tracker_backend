package com.bugtrack.repository;

import com.bugtrack.model.BugAttachment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BugAttachmentRepo extends JpaRepository<BugAttachment, Long> {
  List<BugAttachment> findByBugIdOrderByCreatedAtDesc(Long bugId);
}
