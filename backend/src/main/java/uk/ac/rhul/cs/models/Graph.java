package uk.ac.rhul.cs.models;

import java.util.List;

/**
 * Graph (Data) class.
 *
 * @author Dan Bee
 */
public class Graph {

  private List<Node> nodes;

  private List<Link> links;

  /**
   * Graph constructor method.
   *
   * @param nodes the nodes in the graph
   * @param links the links between the nodes in the graph
   */
  public Graph(List<Node> nodes, List<Link> links) {
    this.nodes = nodes;
    this.links = links;
  }

  public List<Node> getNodes() {
    return nodes;
  }

  public void setNodes(List<Node> nodes) {
    this.nodes = nodes;
  }

  public List<Link> getLinks() {
    return links;
  }

  public void setLinks(List<Link> links) {
    this.links = links;
  }
}
