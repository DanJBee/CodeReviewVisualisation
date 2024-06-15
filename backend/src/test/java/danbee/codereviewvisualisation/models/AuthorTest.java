package danbee.codereviewvisualisation.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AuthorTest {

  private Author author;
  
  @BeforeEach
  void setUp() {
    author = new Author("microsoft", "https://example.com");
  }

  @Test
  void testGetters() {
    assertEquals(author.getAuthorId(), "microsoft");
    assertEquals(author.getAuthorAvatarUrl(), "https://example.com");
    assertEquals(author.getTypeName(), Type.USER);
  }

  @Test
  void testSetters() {
    author.setAuthorId("facebook");
    author.setAuthorAvatarUrl("https://example2.com");
    author.setTypeName(Type.BOT);

    assertEquals(author.getAuthorId(), "facebook");
    assertEquals(author.getAuthorAvatarUrl(), "https://example2.com");
    assertEquals(author.getTypeName(), Type.BOT);
  }
}