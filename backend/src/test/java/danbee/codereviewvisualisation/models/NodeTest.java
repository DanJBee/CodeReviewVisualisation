package danbee.codereviewvisualisation.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NodeTest {

  private Node node;

  @BeforeEach
  void setUp() {
    node = new Node("Test", "https://google.com", "User", 1L);
  }

  @Test
  void testGetters() {
    assertEquals(node.getAuthorId(), "Test");
    assertEquals(node.getAvatarUrl(), "https://google.com");
    assertEquals(node.getTypeName(), "User");
    assertEquals(node.getSize(), 1L);
  }

  @Test
  void testSetters() {
    node.setAuthorId("Testing");
    node.setAvatarUrl("https://google2.com");
    node.setTypeName("Bot");
    node.setSize(2L);

    assertEquals(node.getAuthorId(), "Testing");
    assertEquals(node.getAvatarUrl(), "https://google2.com");
    assertEquals(node.getTypeName(), "Bot");
    assertEquals(node.getSize(), 2L);
  }
}