package uk.ac.rhul.cs.models;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;

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
   * @param createdAt the comment creation date
   */
  public Comment(Timestamp createdAt, PullRequest pullRequest) {
    this.createdAt = createdAt;
    this.pullRequest = pullRequest;
  }

  public Long getId() {
    return id;
  }

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

  public String getDateString() {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    return this.createdAt.toLocalDateTime().format(formatter);
  }
}
