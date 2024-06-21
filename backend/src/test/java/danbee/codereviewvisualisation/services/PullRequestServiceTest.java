package danbee.codereviewvisualisation.services;

import danbee.codereviewvisualisation.models.PullRequest;
import danbee.codereviewvisualisation.repositories.PullRequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
@Transactional
class PullRequestServiceTest {

  @Autowired
  private PullRequestService pullRequestService;

  @Autowired
  private PullRequestRepository pullRequestRepository;

  @BeforeEach
  void before() {
    pullRequestRepository.deleteAll();

    PullRequest pullRequest =
        new PullRequest(1000L, 1, Timestamp.valueOf("2014-10-30 15:15:03"), null);
    pullRequestRepository.save(pullRequest);
  }

  @Test
  void testFindByNumberValid() {
    assertEquals(1000L, pullRequestService.findByNumber(1000L).getNumber());
  }

  @Test
  void testFindByNumberInvalid() {
    assertNull(pullRequestService.findByNumber(0L));
  }

  @Test
  void testFindByProjectIdValid() {
    assertEquals(1, pullRequestService.findByProjectId(1).getFirst().getProjectId());
  }

  @Test
  void testFindByProjectIdInValid() {
    List<PullRequest> projects = new ArrayList<>();
    assertEquals(projects, pullRequestService.findByProjectId(0));
  }

  @Test
  void testFindByCreatedAtValid() {

    assertEquals("2014-10-30 15:15:03.0", pullRequestService
        .findByCreatedAt(Timestamp.valueOf("2014-10-30 15:15:03"))
        .getFirst().getCreatedAt().toString());
  }

  @Test
  void testFindByCreatedAtInvalid() {
    List<PullRequest> pullRequests = new ArrayList<>();
    assertEquals(pullRequests, pullRequestService
        .findByCreatedAt(Timestamp.valueOf("1970-01-01 00:00:00")));
  }
}