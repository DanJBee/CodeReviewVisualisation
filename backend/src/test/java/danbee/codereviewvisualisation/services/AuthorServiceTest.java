package danbee.codereviewvisualisation.services;

import danbee.codereviewvisualisation.models.Author;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class AuthorServiceTest {

  @Autowired
  private AuthorService authorService;

  @Test
  void testFindAll() {
    List<Author> authors = authorService.findAll();
    String authorId = authors.getFirst().getAuthorId();
    assertEquals("0o001", authorId);
    String avatarUrl = authors.getFirst().getAvatarUrl();
    assertEquals("https://avatars.githubusercontent.com/u/4294069?u=a8f5a39abb0c0cefce2d9cca29a0733d1af3ed77&v=4", avatarUrl);
  }

  @Test
  void testFindByAuthorIdValid() {
    assertEquals("0o001", authorService
        .findByAuthorId("0o001")
        .getAuthorId());
  }

  @Test
  void testFindByAuthorIdInvalid() {
    assertThrows(NoSuchElementException.class, () -> authorService
        .findByAuthorId("invalidUsername"));
  }
}