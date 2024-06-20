package danbee.codereviewvisualisation.models;

import jakarta.persistence.*;

import java.sql.Timestamp;

/**
 * Comment entity class.
 *
 * @author Dan Bee
 */
@Entity
@Table(name = "comments")
public class Comment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

//  private Long number;

  // private String authorId;

  private Timestamp createdAt;

  @ManyToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "pull_request")
  private PullRequest pullRequest;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "author_id")
  private Author author;

  public Comment() {
  }

  /**
   * Comment constructor method.
   *
   * @param number    the comment pull request number
   * @param authorId  the comment author ID
   * @param createdAt the comment creation date
   */
  public Comment(Long number, String authorId, Timestamp createdAt, PullRequest pullRequest) {
//    this.number = number;
//    this.authorId = authorId;
    this.createdAt = createdAt;
    this.pullRequest = pullRequest;
  }

  public Long getId() {
    return id;
  }

//  public Long getNumber() {
//    return number;
//  }
//
//  public void setNumber(Long number) {
//    this.number = number;
//  }

//  public String getAuthorId() {
//    return authorId;
//  }
//
//  public void setAuthorId(String authorId) {
//    this.authorId = authorId;
//  }

  public Timestamp getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Timestamp createdAt) {
    this.createdAt = createdAt;
  }

  public PullRequest getPullRequest() {
    return pullRequest;
  }

  public void setPullRequest(PullRequest pullRequest) {
    this.pullRequest = pullRequest;
  }

  public Author getAuthor() {
    return author;
  }

  public void setAuthor(Author author) {
    this.author = author;
  }
}
