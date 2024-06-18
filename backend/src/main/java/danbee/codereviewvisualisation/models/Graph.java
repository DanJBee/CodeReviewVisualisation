package danbee.codereviewvisualisation.models;

import java.util.Map;

/**
 * Graph (Data) class.
 *
 * @author Dan Bee
 */
public class Graph {

  private Map<String, Node> nodes;

  private Map<String, Link> links;

  /**
   * Graph constructor method.
   *
   * @param nodes the nodes in the graph
   * @param links the links between the nodes in the graph
   */
  public Graph(Map<String, Node> nodes, Map<String, Link> links) {
    this.nodes = nodes;
    this.links = links;
  }

  public Map<String, Node> getNodes() {
    return nodes;
  }

  public void setNodes(Map<String, Node> nodes) {
    this.nodes = nodes;
  }

  public Map<String, Link> getLinks() {
    return links;
  }

  public void setLinks(Map<String, Link> links) {
    this.links = links;
  }
}
