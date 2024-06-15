package danbee.codereviewvisualisation.models;

/**
 * Node class.
 *
 * @author Dan Bee
 */
public class Node {

  private String id;

  private long number;

  public Node(String id, long number) {
    this.id = id;
    this.number = number;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public long getNumber() {
    return number;
  }

  public void setNumber(long number) {
    this.number = number;
  }
}
