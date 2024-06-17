package danbee.codereviewvisualisation.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;

/**
 * Project entity class.
 *
 * @author Dan Bee
 */
@Entity
@Table(name = "projects")
public class Project {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String owner;

  private String repository;

  public Project() {
  }

  /**
   * Project constructor method.
   *
   * @param owner      the project owner
   * @param repository the project repository
   */
  public Project(String owner, String repository) {
    this.owner = owner;
    this.repository = repository;
  }

  public Long getId() {
    return id;
  }

  public String getOwner() {
    return owner;
  }

  public void setOwner(String owner) {
    this.owner = owner;
  }

  public String getRepository() {
    return repository;
  }

  public void setRepository(String repository) {
    this.repository = repository;
  }

  /**
   * Project toString method.
   *
   * @return the Project object mapped as a JSON object
   */
  @Override
  public String toString() {
    ObjectMapper mapper = new ObjectMapper();
    try {
      return mapper.writeValueAsString(this);
    } catch (JsonProcessingException exception) {
      exception.getMessage();
      return super.toString();
    }
  }
}
