package danbee.codereviewvisualisation.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class CommentServiceTest {

  @Autowired
  private CommentService commentService;

  @Test
  void testFindAll() {

  }

//  @Test
//  void testFindByNumberValid() {
//    assertEquals(1000L, commentService.findByNumber(1000L).getFirst().getNumber());
//  }
//
//  @Test
//  void testFindByNumberInvalid() {
//    List<Comment> comments = new ArrayList<>();
//    assertEquals(commentService.findByNumber(0L), comments);
//  }

//  @Test
//  void testFindByAuthorIdValid() {
//    assertEquals("jrieken",
//        commentService.findByAuthorId("jrieken").getFirst().getAuthor().getAuthorId());
//  }
//
//  @Test
//  void testFindByAuthorIdInvalid() {
//    List<Comment> comments = new ArrayList<>();
//    assertEquals(commentService.findByAuthorId("invalidUsername"), comments);
//  }

  @Test
  void testFindByCreatedAtValid() {
    assertEquals("2014-10-30 16:51:39.0", commentService
        .findByCreatedAt(Timestamp.valueOf("2014-10-30 16:51:39"))
        .getFirst().getCreatedAt().toString());
  }

  @Test
  void testFindByCreatedAtInvalid() {
    assertThrows(NoSuchElementException.class, () -> commentService
        .findByCreatedAt(Timestamp.valueOf("1970-01-01 00:00:00"))
        .getFirst().getCreatedAt().toString());
  }
}