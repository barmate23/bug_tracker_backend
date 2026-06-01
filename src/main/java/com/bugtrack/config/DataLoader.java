package com.bugtrack.config;

import com.bugtrack.model.Bug;
import com.bugtrack.model.BugStatus;
import com.bugtrack.model.Comment;
import com.bugtrack.model.Priority;
import com.bugtrack.model.Project;
import com.bugtrack.model.Role;
import com.bugtrack.model.User;
import com.bugtrack.model.UserProject;
import com.bugtrack.repository.BugRepo;
import com.bugtrack.repository.CommentRepo;
import com.bugtrack.repository.ProjectRepo;
import com.bugtrack.repository.UserProjectRepo;
import com.bugtrack.repository.UserRepo;
import java.time.Instant;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {
  private final UserRepo userRepo;
  private final ProjectRepo projectRepo;
  private final UserProjectRepo userProjectRepo;
  private final BugRepo bugRepo;
  private final CommentRepo commentRepo;

  public DataLoader(UserRepo userRepo, ProjectRepo projectRepo, UserProjectRepo userProjectRepo, BugRepo bugRepo,
      CommentRepo commentRepo) {
    this.userRepo = userRepo;
    this.projectRepo = projectRepo;
    this.userProjectRepo = userProjectRepo;
    this.bugRepo = bugRepo;
    this.commentRepo = commentRepo;
  }

  @Override
  public void run(String... args) {
    User admin = userRepo.save(new User("admin", "admin123", "Anaya Rao", Role.ADMIN));
    User dev = userRepo.save(new User("dev", "dev123", "Rohan Mehta", Role.DEV));
    User tester = userRepo.save(new User("tester", "tester123", "Mira Sen", Role.TESTER));

    Project apollo = projectRepo.save(new Project("Apollo Revamp", "Customer portal redesign and migration work."));
    Project mobile = projectRepo.save(new Project("Mobile Checkout", "Native checkout stability and payment fixes."));

    userProjectRepo.save(new UserProject(dev, apollo));
    userProjectRepo.save(new UserProject(tester, apollo));
    userProjectRepo.save(new UserProject(tester, mobile));

    Bug b1 = bug(apollo, "Checkout button becomes disabled after failed validation",
        "When a required address field is missing, the checkout button remains disabled after the user fixes the field.",
        BugStatus.OPEN, Priority.CRITICAL, tester, dev);
    Bug b2 = bug(apollo, "Attachment preview overflows on small screens",
        "Image previews in the issue drawer overflow the viewport on widths under 420px.",
        BugStatus.IN_PROGRESS, Priority.HIGH, dev, dev);
    Bug b3 = bug(mobile, "Payment retry does not show confirmation",
        "Successful retry after a declined card closes the sheet without the success confirmation state.",
        BugStatus.RESOLVED, Priority.MEDIUM, tester, tester);

    bugRepo.save(b1);
    bugRepo.save(b2);
    bugRepo.save(b3);
    commentRepo.save(new Comment(b1, admin, "Please prioritize this for the next patch window."));
    commentRepo.save(new Comment(b1, dev, "I can reproduce it locally and will trace the form state."));
  }

  private Bug bug(Project project, String title, String description, BugStatus status, Priority priority, User creator,
      User assignee) {
    Bug bug = new Bug();
    bug.setProject(project);
    bug.setTitle(title);
    bug.setDescription(description);
    bug.setStatus(status);
    bug.setPriority(priority);
    bug.setCreatedBy(creator);
    bug.setAssignedTo(assignee);
    bug.setCreatedAt(Instant.now());
    bug.setUpdatedAt(Instant.now());
    return bug;
  }
}
