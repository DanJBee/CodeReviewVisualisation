package danbee.codereviewvisualisation.models;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GraphTest {

  private Graph graph;

  private Map<String, Node> nodes;

  private Map<String, Link> links;

//  @BeforeEach
//  void setUp() {
//    nodes = new HashMap<>();
//    links = new HashMap<>();
//    Node sourceNode = new Node("Source Node", "https://google.com", "User", 1L);
//    Node targetNode = new Node("Target Node", "https://google2.com", "User", 2L);
//    Node node = new Node("Test Node", "https://google3.com", "User", 3L);
//    nodes.add(node);
//    Link link = new Link(sourceNode, targetNode, 4);
//    links.add(link);
//    graph = new Graph(nodes, links);
//  }

  @Test
  @Disabled
  void testGetters() {
    assertEquals(graph.getNodes(), nodes);
    assertEquals(graph.getLinks(), links);
  }

  @Test
  @Disabled
  void testSetters() {
    Map<String, Node> newNodes = new HashMap<>();
    Map<String, Link> newLinks = new HashMap<>();
    graph.setNodes(newNodes);
    graph.setLinks(newLinks);

    assertEquals(graph.getNodes(), newNodes);
    assertEquals(graph.getLinks(), newLinks);
  }
}