package uk.ac.rhul.cs.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GraphTest {

  private Graph graph;

  private List<Node> nodes;

  private List<Link> links;

  @BeforeEach
  void beforeEach() {
    nodes = new ArrayList<>();
    links = new ArrayList<>();
    Node sourceNode = new Node("Source Node", "https://google.com", 1L);
    Node targetNode = new Node("Target Node", "https://google2.com", 2L);
    Node node = new Node("Test Node", "https://google3.com", 3L);
    nodes.add(node);
    Link link = new Link(sourceNode, targetNode, 4);
    links.add(link);
    graph = new Graph(nodes, links);
  }

  @Test
  void testGetters() {
    assertEquals(graph.getNodes(), nodes);
    assertEquals(graph.getLinks(), links);
  }

  @Test
  void testSetters() {
    List<Node> newNodes = new ArrayList<>();
    List<Link> newLinks = new ArrayList<>();
    graph.setNodes(newNodes);
    graph.setLinks(newLinks);

    assertEquals(graph.getNodes(), newNodes);
    assertEquals(graph.getLinks(), newLinks);
  }
}