package uk.ac.rhul.cs.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class PullRequestTest {

  private final Timestamp timestamp = new Timestamp(1);

  private final List<Comment> comments = new ArrayList<>();

  private PullRequest pullRequest;

  @BeforeEach
  void setUp() {
    pullRequest = new PullRequest(1L, 2, timestamp, comments);
  }

  @Test
  void testGetters() {
    assertNull(pullRequest.getId());
    assertEquals(pullRequest.getNumber(), 1L);
    assertEquals(pullRequest.getProjectId(), 2);
    assertEquals(pullRequest.getCreatedAt(), timestamp);
    assertEquals(pullRequest.getComments(), comments);
  }

  @Test
  void testSetters() {
    pullRequest.setNumber(2L);
    pullRequest.setProjectId(3);
    Timestamp newTimestamp = new Timestamp(4);
    pullRequest.setCreatedAt(newTimestamp);

    assertNull(pullRequest.getId());
    assertEquals(pullRequest.getNumber(), 2L);
    assertEquals(pullRequest.getProjectId(), 3);
    assertEquals(pullRequest.getCreatedAt(), newTimestamp);
    assertEquals(pullRequest.getComments(), comments);
  }
}