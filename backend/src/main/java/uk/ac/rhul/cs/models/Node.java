package uk.ac.rhul.cs.models;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

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

  private transient List<String> commentDates;

  // Workload: number of open PRs for this author
  private int openPrCount;

  // Maximum allowed open PRs before user is considered overloaded
  private static final int MAX_WORKLOAD = 5;

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
    this.openPrCount = 0;

    this.commentDates = new ArrayList<String>();
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

  /**
   * It takes a string of a comment creation date
   *
   * @param commentDate "yyyy-MM-dd" format.
   */
  public void addCommentDate(String commentDate) {
    this.commentDates.add(commentDate);
  }

  public Integer getNumberOfCommentDates() {
    return new HashSet<String>(this.commentDates).size();
  }

  public int getOpenPrCount() {
    return openPrCount;
  }

  public void setOpenPrCount(int openPrCount) {
    this.openPrCount = openPrCount;
  }

  public void incrementOpenPrCount() {
    this.openPrCount++;
  }

  /**
   * Returns the workload status as a value between 0 and 1.
   * 0 = fully loaded (red), 1 = available (green)
   */
  public double getWorkloadValue() {
    if (openPrCount >= MAX_WORKLOAD) {
      return 0.0; // Fully loaded
    }
    return 1.0 - ((double) openPrCount / MAX_WORKLOAD);
  }

  /**
   * Returns true if the user has reached maximum workload
   */
  public boolean isOverloaded() {
    return openPrCount >= MAX_WORKLOAD;
  }

  @Override
  public boolean equals(Object obj) {
    Node comparingNode = (Node) obj;
    return this.authorId.equals(comparingNode.getAuthorId());
  }


}
