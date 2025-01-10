package uk.ac.rhul.cs.models;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Link class.
 *
 * @author Dan Bee
 */
public class Link {

  private Node source;

  private Node target;

  private long thickness;

  private transient double colourValue;

  private transient List<String> commentDates;

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

    this.commentDates = new ArrayList<String>();
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

  public double getColourValue() {
    return colourValue;
  }

  public void setColourValue(double colourValue) {
    this.colourValue = colourValue;
  }

  /**
   * It takes a string of a comment creation date
   *
   * @param commentDate "yyyy-MM-dd" format.
   */
  public void addCommentDate(String commentDate) {
    this.commentDates.add(commentDate);
  }

  public Integer getNumberOfCommentDates() {
    return new HashSet<String>(this.commentDates).size();
  }

}
