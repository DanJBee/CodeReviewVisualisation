package uk.ac.rhul.cs.models;

/**
 * Link class.
 *
 * @author Dan Bee
 */
public class Link {

  private Node source;

  private Node target;

  private long thickness;

  /**
   * Link constructor method.
   *
   * @param source    the source node of the link
   * @param target    the target node of the link
   * @param thickness the thickness of the link
   */
  public Link(Node source, Node target, long thickness) {
    this.source = source;
    this.target = target;
    this.thickness = thickness;
  }

  public Node getSource() {
    return source;
  }

  public void setSource(Node source) {
    this.source = source;
  }

  public Node getTarget() {
    return target;
  }

  public void setTarget(Node target) {
    this.target = target;
  }

  public long getThickness() {
    return thickness;
  }

  public void setThickness(long thickness) {
    this.thickness = thickness;
  }
}
