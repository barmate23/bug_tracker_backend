package com.bugtrack.controller;

import com.bugtrack.dto.ApiResponse;
import com.bugtrack.dto.AttachmentDto;
import com.bugtrack.model.Bug;
import com.bugtrack.model.BugAttachment;
import com.bugtrack.repository.BugAttachmentRepo;
import com.bugtrack.repository.BugRepo;
import com.bugtrack.service.AccessService;
import com.bugtrack.service.AuthService;
import com.bugtrack.service.SessionUser;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/bugs/{bugId}/attachments")
public class AttachmentController {
  private final BugAttachmentRepo attachmentRepo;
  private final BugRepo bugRepo;
  private final AuthService authService;
  private final AccessService accessService;

  public AttachmentController(BugAttachmentRepo attachmentRepo, BugRepo bugRepo, AuthService authService,
      AccessService accessService) {
    this.attachmentRepo = attachmentRepo;
    this.bugRepo = bugRepo;
    this.authService = authService;
    this.accessService = accessService;
  }

  @GetMapping
  public ApiResponse<List<AttachmentDto>> list(@PathVariable Long bugId, HttpSession session) {
    loadAccessibleBug(bugId, authService.current(session));
    return ApiResponse.ok(attachmentRepo.findByBugIdOrderByCreatedAtDesc(bugId).stream()
        .map(AttachmentDto::from)
        .toList());
  }

  @PostMapping
  public ApiResponse<AttachmentDto> upload(@PathVariable Long bugId, @RequestParam("file") MultipartFile file,
      HttpSession session) throws IOException {
    Bug bug = loadAccessibleBug(bugId, authService.current(session));
    if (file.isEmpty()) {
      throw new IllegalArgumentException("File is required");
    }
    if (file.getContentType() == null || !file.getContentType().startsWith("image/")) {
      throw new IllegalArgumentException("Only image files are allowed");
    }
    BugAttachment attachment = new BugAttachment();
    attachment.setBug(bug);
    attachment.setFileName(file.getOriginalFilename() == null ? "image" : file.getOriginalFilename());
    attachment.setContentType(file.getContentType());
    attachment.setSizeBytes(file.getSize());
    attachment.setData(file.getBytes());
    return ApiResponse.ok(AttachmentDto.from(attachmentRepo.save(attachment)), "Image uploaded");
  }

  @GetMapping("/{attachmentId}/content")
  public ResponseEntity<byte[]> content(@PathVariable Long bugId, @PathVariable Long attachmentId, HttpSession session) {
    loadAccessibleBug(bugId, authService.current(session));
    BugAttachment attachment = attachmentRepo.findById(attachmentId)
        .filter(found -> found.getBug().getId().equals(bugId))
        .orElseThrow(() -> new EntityNotFoundException("Attachment not found"));
    return ResponseEntity.ok()
        .cacheControl(CacheControl.noCache())
        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + attachment.getFileName() + "\"")
        .contentType(MediaType.parseMediaType(attachment.getContentType()))
        .body(attachment.getData());
  }

  private Bug loadAccessibleBug(Long bugId, SessionUser current) {
    Bug bug = bugRepo.findById(bugId).orElseThrow(() -> new EntityNotFoundException("Bug not found"));
    accessService.requireBugAccess(current, bug);
    return bug;
  }
}
