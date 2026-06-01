package com.bugtrack.dto;

import com.bugtrack.model.BugAttachment;
import java.time.Instant;

public record AttachmentDto(
    Long id,
    Long bugId,
    String fileName,
    String contentType,
    Long sizeBytes,
    Instant createdAt,
    String url
) {
  public static AttachmentDto from(BugAttachment attachment) {
    Long bugId = attachment.getBug().getId();
    return new AttachmentDto(
        attachment.getId(),
        bugId,
        attachment.getFileName(),
        attachment.getContentType(),
        attachment.getSizeBytes(),
        attachment.getCreatedAt(),
        "/api/bugs/" + bugId + "/attachments/" + attachment.getId() + "/content"
    );
  }
}
