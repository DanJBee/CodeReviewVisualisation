package danbee.codereviewvisualisation.models;

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
  void setUp() {
    nodes = new ArrayList<>();
    links = new ArrayList<>();
    Node sourceNode = new Node("Source Node", 1);
    Node targetNode = new Node("Target Node", 2);
    Node node = new Node("Test Node", 3);
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