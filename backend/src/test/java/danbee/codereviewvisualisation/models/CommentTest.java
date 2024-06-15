package danbee.codereviewvisualisation.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class CommentTest {

  private final Timestamp timestamp = new Timestamp(2);
  private Comment comment;

  @BeforeEach
  void setUp() {
    comment = new Comment(1L, "microsoft", timestamp);
  }

  @Test
  void testGetters() {
    assertNull(comment.getId());
    assertEquals(comment.getNumber(), 1L);
    assertEquals(comment.getAuthorId(), "microsoft");
    assertEquals(comment.getCreatedAt(), timestamp);
  }

  @Test
  void testSetters() {
    Timestamp newTimestamp = new Timestamp(3);
    comment.setNumber(2L);
    comment.setAuthorId("facebook");
    comment.setCreatedAt(newTimestamp);

    assertNull(comment.getId());
    assertEquals(comment.getNumber(), 2L);
    assertEquals(comment.getAuthorId(), "facebook");
    assertEquals(comment.getCreatedAt(), newTimestamp);
  }
}