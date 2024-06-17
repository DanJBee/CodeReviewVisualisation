package danbee.codereviewvisualisation.models;

/**
 * Node class.
 *
 * @author Dan Bee
 */
public class Node {

  private Long size;

  private String authorId;

  private String avatarUrl;

  private String typeName;

  /**
   * Node constructor method.
   *
   * @param authorId  the author's ID
   * @param avatarUrl the author's avatar URL
   * @param typeName  the author's type name
   */
  public Node(String authorId, String avatarUrl, String typeName, Long size) {
    this.authorId = authorId;
    this.avatarUrl = avatarUrl;
    this.typeName = typeName;
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

  public String getTypeName() {
    return typeName;
  }

  public void setTypeName(String typeName) {
    this.typeName = typeName;
  }

  public Long getSize() {
    return size;
  }

  public void setSize(Long size) {
    this.size = size;
  }
}
