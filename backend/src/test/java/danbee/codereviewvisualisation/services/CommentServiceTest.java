package danbee.codereviewvisualisation.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import danbee.codereviewvisualisation.models.Author;
import danbee.codereviewvisualisation.models.Comment;
import danbee.codereviewvisualisation.repositories.CommentRepository;
import java.sql.Timestamp;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class CommentServiceTest {

  @Autowired
  private CommentService commentService;

  @Autowired
  private CommentRepository commentRepository;

  @BeforeEach
  void beforeEach() {
    commentRepository.deleteAll();

    Comment comment = new Comment();
    comment.setAuthor(new Author("tester", "avatar_url"));
    comment.setCreatedAt(Timestamp.valueOf("2014-10-30 16:51:39"));
    commentRepository.save(comment);
  }

  @Test
  void testFindAll() {

  }

  @Test
  void testFindByCreatedAtValid() {


    assertEquals("2014-10-30 16:51:39.0",
        commentService.findByCreatedAt(Timestamp.valueOf("2014-10-30 16:51:39")).getFirst()
            .getCreatedAt().toString());
  }

  @Test
  void testFindByCreatedAtInvalid() {
    assertThrows(NoSuchElementException.class,
        () -> commentService.findByCreatedAt(Timestamp.valueOf("1970-01-01 00:00:00")).getFirst()
            .getCreatedAt().toString());
  }
}
