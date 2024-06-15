package danbee.codereviewvisualisation.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LinkTest {

  private Link link;

  private Node sourceNode;

  private Node targetNode;

  @BeforeEach
  void setUp() {
    link = new Link(sourceNode, targetNode, 1);
    sourceNode = new Node("Source Node", 2);
    targetNode = new Node("Target Node", 3);
  }

  @Test
  void testGetters() {
    assertEquals(link.getSource(), sourceNode);
    assertEquals(link.getTarget(), targetNode);
    assertEquals(link.getThickness(), 1);
  }

  @Test
  void testSetters() {
    Node newSourceNode = new Node("New Source Node", 4);
    Node newTargetNode = new Node("New Target Node", 5);
    link.setSource(newSourceNode);
    link.setTarget(newTargetNode);
    link.setThickness(6);

    assertEquals(link.getSource(), newSourceNode);
    assertEquals(link.getTarget(), newTargetNode);
    assertEquals(link.getThickness(), 6);
  }
}