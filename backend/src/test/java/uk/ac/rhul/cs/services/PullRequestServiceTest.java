package uk.ac.rhul.cs.services;

import uk.ac.rhul.cs.models.Comment;
import uk.ac.rhul.cs.models.PullRequest;
import uk.ac.rhul.cs.repositories.CommentRepository;
import uk.ac.rhul.cs.repositories.PullRequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
class PullRequestServiceTest {

  @Autowired
  private PullRequestService pullRequestService;

  @Autowired
  private PullRequestRepository pullRequestRepository;

  @Autowired
  private CommentRepository commentRepository;

  @BeforeEach
  void beforeEach() {
    pullRequestRepository.deleteAll();

    commentRepository.deleteAll();

    List<Comment> comments = new ArrayList<>();

    PullRequest pullRequest =
        new PullRequest(1000L, 1, Timestamp.valueOf("2014-10-30 15:15:03"), comments);
    pullRequestRepository.save(pullRequest);

    Comment comment = new Comment(Timestamp.valueOf("2014-10-30 16:15:03"), pullRequest);
    comments.add(comment);
    commentRepository.save(comment);
  }

  @Test
  void testFindPullRequestsByProjectIdValid() {
    List<PullRequest> pullRequests = pullRequestService.findPullRequestsByProjectId(
        Timestamp.valueOf("2014-10-30 15:15:03"),
        Timestamp.valueOf("2014-10-30 15:15:03"),
        1);
    PullRequest pullRequest = pullRequests.getFirst();
    assertEquals(1000L, pullRequest.getNumber());
    assertEquals(1, pullRequest.getProjectId());
    assertEquals(Timestamp.valueOf("2014-10-30 15:15:03"), pullRequest.getCreatedAt());
    assertEquals(1, pullRequest.getComments().size());
  }
}