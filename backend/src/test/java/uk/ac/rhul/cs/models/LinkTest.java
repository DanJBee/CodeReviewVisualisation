package uk.ac.rhul.cs.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LinkTest {

  private Link link;

  private Node sourceNode;

  private Node targetNode;

  @BeforeEach
  void setUp() {
    sourceNode = new Node("Source Node", "https://google.com", 1L);
    targetNode = new Node("Target Node", "https://google2.com", 2L);
    link = new Link(sourceNode, targetNode, 1);
  }

  @Test
  void testGetters() {
    assertEquals(link.getSource(), sourceNode);
    assertEquals(link.getTarget(), targetNode);
    assertEquals(link.getThickness(), 1);
  }

  @Test
  void testSetters() {
    Node newSourceNode = new Node("New Source Node", "https://google3.com", 3L);
    Node newTargetNode = new Node("New Target Node", "https://google4.com", 4L);
    link.setSource(newSourceNode);
    link.setTarget(newTargetNode);
    link.setThickness(6);

    assertEquals(link.getSource(), newSourceNode);
    assertEquals(link.getTarget(), newTargetNode);
    assertEquals(link.getThickness(), 6);
  }
}