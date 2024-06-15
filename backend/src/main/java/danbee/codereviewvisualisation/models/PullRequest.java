package danbee.codereviewvisualisation.models;

import jakarta.persistence.*;

import java.sql.Timestamp;

/**
 * Pull request entity class.
 *
 * @author Dan Bee
 */
@Entity
@Table(name = "pull_requests")
public class PullRequest {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long number;

  private Integer projectId;

  private Timestamp createdAt;

  private String authorId;

  public PullRequest() {
  }

  /**
   * Pull request constructor method.
   *
   * @param number    the pull request number
   * @param projectId the pull request project ID
   * @param createdAt the pull request creation timestamp
   * @param authorId  the pull request author ID
   */
  public PullRequest(Long number, Integer projectId, Timestamp createdAt, String authorId) {
    this.number = number;
    this.projectId = projectId;
    this.createdAt = createdAt;
    this.authorId = authorId;
  }

  public Long getId() {
    return id;
  }

  public Long getNumber() {
    return number;
  }

  public void setNumber(Long number) {
    this.number = number;
  }

  public Integer getProjectId() {
    return projectId;
  }

  public void setProjectId(Integer projectId) {
    this.projectId = projectId;
  }

  public Timestamp getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Timestamp createdAt) {
    this.createdAt = createdAt;
  }

  public String getAuthorId() {
    return authorId;
  }

  public void setAuthorId(String authorId) {
    this.authorId = authorId;
  }
}
