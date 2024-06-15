package danbee.codereviewvisualisation.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class AuthorServiceTest {

  @Autowired
  private AuthorService authorService;

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