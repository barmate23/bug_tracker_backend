package com.bugtrack.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "comments")
public class Comment {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "bug_id", nullable = false)
  private Bug bug;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(nullable = false, length = 2000)
  private String message;

  @Column(nullable = false)
  private Instant createdAt = Instant.now();

  public Comment() {}

  public Comment(Bug bug, User user, String message) {
    this.bug = bug;
    this.user = user;
    this.message = message;
  }

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public Bug getBug() { return bug; }
  public void setBug(Bug bug) { this.bug = bug; }
  public User getUser() { return user; }
  public void setUser(User user) { this.user = user; }
  public String getMessage() { return message; }
  public void setMessage(String message) { this.message = message; }
  public Instant getCreatedAt() { return createdAt; }
  public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
