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

  private long duration;

  /**
   * Graph constructor method.
   *
   * @param nodes the nodes in the graph
   * @param links the links between the nodes in the graph
   */
  public Graph(List<Node> nodes, List<Link> links, long duration) {
    this.nodes = nodes;
    this.links = links;
    this.duration = duration;
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

  public long getDuration() {
    return duration;
  }

  public void setDuration(long duration) {
    this.duration = duration;
  }
}
