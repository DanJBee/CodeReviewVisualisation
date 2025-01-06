package uk.ac.rhul.cs.models;

import java.beans.Transient;

/**
 * Node class.
 *
 * @author Dan Bee
 */
public class Node {

  private Long size;

  private String authorId;

  private String avatarUrl;

  private transient double colourValue;

  private transient int count;

  /**
   * Node constructor method.
   *
   * @param authorId  the author's ID
   * @param avatarUrl the author's avatar URL
   */
  public Node(String authorId, String avatarUrl, Long size) {
    this.authorId = authorId;
    this.avatarUrl = avatarUrl;
    this.size = size;
  }

  public String getAuthorId() {
    return authorId;
  }

  public void setAuthorId(String authorId) {
    this.authorId = authorId;
  }

  public String getAvatarUrl() {
    return avatarUrl;
  }

  public void setAvatarUrl(String avatarUrl) {
    this.avatarUrl = avatarUrl;
  }
  
  public Long getSize() {
    return size;
  }

  public void setSize(Long size) {
    this.size = size;
  }

  public double getColourValue() {
    return colourValue;
  }

  public void setColourValue(double colourValue) {
    this.colourValue = colourValue;
  }

  public int getCount() {
    return count;
  }

  public void setCount(int count) {
    this.count = count;
  }
}
