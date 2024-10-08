package uk.ac.rhul.cs.models;

import jakarta.persistence.*;

import java.sql.Timestamp;
import java.util.List;

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

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "author_id")
  private Author author;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "pullRequest")
  private List<Comment> comments;

  public PullRequest() {
  }

  /**
   * Pull request constructor method.
   *
   * @param number    the pull request number
   * @param projectId the pull request project ID
   * @param createdAt the pull request creation timestamp
   */
  public PullRequest(Long number, Integer projectId, Timestamp createdAt, List<Comment> comments) {
    this.number = number;
    this.projectId = projectId;
    this.createdAt = createdAt;
    this.comments = comments;
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

  public Author getAuthor() {
    return author;
  }

  public void setAuthor(Author author) {
    this.author = author;
  }

  public List<Comment> getComments() {
    return comments;
  }
}
