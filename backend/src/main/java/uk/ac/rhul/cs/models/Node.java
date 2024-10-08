package uk.ac.rhul.cs.models;

/**
 * Node class.
 *
 * @author Dan Bee
 */
public class Node {

  private Long size;

  private String authorId;

  private String avatarUrl;

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
}
