package danbee.codereviewvisualisation.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Author entity class.
 *
 * @author Dan Bee
 */
@Entity
@Table(name = "authors")
public class Author {

  @Id
  private String authorId;

  private String authorAvatarUrl;

  private Type typeName;

  public Author() {
  }

  /**
   * Author constructor method.
   *
   * @param authorId        the author ID
   * @param authorAvatarUrl the author avatar URL
   */
  public Author(String authorId, String authorAvatarUrl) {
    this.authorId = authorId;
    this.authorAvatarUrl = authorAvatarUrl;
    this.typeName = Type.USER;
  }

  public String getAuthorId() {
    return authorId;
  }

  public void setAuthorId(String authorId) {
    this.authorId = authorId;
  }

  public String getAuthorAvatarUrl() {
    return authorAvatarUrl;
  }

  public void setAuthorAvatarUrl(String authorAvatarUrl) {
    this.authorAvatarUrl = authorAvatarUrl;
  }

  public Type getTypeName() {
    return typeName;
  }

  public void setTypeName(Type typeName) {
    this.typeName = typeName;
  }
}
