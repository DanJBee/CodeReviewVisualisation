package danbee.codereviewvisualisation.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NodeTest {

  private Node node;

  @BeforeEach
  void setUp() {
    node = new Node("Test", 1);
  }

  @Test
  void testGetters() {
    assertEquals(node.getId(), "Test");
    assertEquals(node.getNumber(), 1);
  }

  @Test
  void testSetters() {
    node.setId("Testing");
    node.setNumber(2);

    assertEquals(node.getId(), "Testing");
    assertEquals(node.getNumber(), 2);
  }
}