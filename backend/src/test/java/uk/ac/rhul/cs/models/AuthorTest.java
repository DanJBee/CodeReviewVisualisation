package uk.ac.rhul.cs.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class AuthorTest {

  private Author author;

  @BeforeEach
  void setUp() {
    author = new Author("microsoft", "https://example.com");
  }

  @Test
  void testEmptyConstructor() {
    Author emptyAuthor = new Author();
    assertNull(emptyAuthor.getAuthorId());
    assertNull(emptyAuthor.getAuthorId());
  }

  @Test
  void testGetters() {
    assertEquals(author.getAuthorId(), "microsoft");
    assertEquals(author.getAvatarUrl(), "https://example.com");
  }

  @Test
  void testSetters() {
    author.setAuthorId("facebook");
    author.setAuthorAvatarUrl("https://example2.com");

    assertEquals(author.getAuthorId(), "facebook");
    assertEquals(author.getAvatarUrl(), "https://example2.com");
  }
}