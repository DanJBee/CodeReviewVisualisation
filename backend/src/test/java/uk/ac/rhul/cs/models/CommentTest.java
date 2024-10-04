package uk.ac.rhul.cs.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class CommentTest {

  private final Timestamp timestamp = new Timestamp(2);

  private final PullRequest pullRequest = new PullRequest();

  private Comment comment;

  @BeforeEach
  void setUp() {
    comment = new Comment(timestamp, pullRequest);
  }

  @Test
  void testGetters() {
    assertNull(comment.getId());
    assertEquals(comment.getCreatedAt(), timestamp);
    assertEquals(comment.getPullRequest(), pullRequest);
  }

  @Test
  void testSetters() {
    Timestamp newTimestamp = new Timestamp(3);
    comment.setCreatedAt(newTimestamp);
    PullRequest newPullRequest = new PullRequest();
    comment.setPullRequest(newPullRequest);

    assertNull(comment.getId());
    assertEquals(comment.getCreatedAt(), newTimestamp);
    assertEquals(comment.getPullRequest(), newPullRequest);
  }
}