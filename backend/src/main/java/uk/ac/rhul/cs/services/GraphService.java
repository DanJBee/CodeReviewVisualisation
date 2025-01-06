package uk.ac.rhul.cs.services;

import org.springframework.stereotype.Service;
import uk.ac.rhul.cs.models.*;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Graph service class.
 *
 * @author Dan Bee
 */
@Service
public class GraphService {

  private final PullRequestService pullRequestService;

  private final ProjectService projectService;

  /**
   * Graph service controller method.
   */
  public GraphService(PullRequestService pullRequestService, ProjectService projectService) {
    this.pullRequestService = pullRequestService;
    this.projectService = projectService;
  }

  /**
   * getGraphData method.
   *
   * @param owner   the owner of the project
   * @param project the project name
   * @param start   the start time of the graph data
   * @param end     the end time of the graph data
   * @return the graph data
   */
  public Graph getGraphData(String owner, String project, String start, String end) {
    Integer id = projectService.findByOwnerAndRepository(owner, project).getId();
    DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

    if (start == null || end == null) {
      Instant now = Instant.now();
      Instant nowMonthAgo = now.minusSeconds(60 * 60 * 24 * 30);

      start = nowMonthAgo.toString();
      end = now.toString();
    }

    LocalDateTime startDate = LocalDateTime.parse(start, formatter);
    LocalDateTime endDate = LocalDateTime.parse(end, formatter);

    Duration duration = Duration.between(startDate, endDate);
    long durationInDays = duration.toDays();

    Timestamp startTimestamp = Timestamp.from(Instant.parse(start));
    Timestamp endTimestamp = Timestamp.from(Instant.parse(end));

    List<PullRequest> pullRequests = pullRequestService
        .findPullRequestsByProjectId(
            startTimestamp,
            endTimestamp,
            id
        );
    List<Node> nodes = new ArrayList<>();
    List<Link> links = new ArrayList<>();
    Map<String, Integer> authorCount = new HashMap<>();
    for (PullRequest pullRequest : pullRequests) {
      if (pullRequest.getAuthor().getAuthorId().equals("typescript-bot")) {
        continue;
      }

      authorCount.put(pullRequest.getAuthor().getAuthorId(),
          authorCount.getOrDefault(pullRequest.getAuthor().getAuthorId(), 0) + 1);

      if (nodes.stream().noneMatch(node ->
          node.getAuthorId().equals(pullRequest.getAuthor().getAuthorId()))) {
        nodes.add(
            new Node(
                pullRequest.getAuthor().getAuthorId(),
                pullRequest.getAuthor().getAvatarUrl(),
                0L
            )
        );
      }

      for (Comment comment : pullRequest.getComments()) {
        if (comment.getAuthor() != null
            && comment.getAuthor().getAuthorId().equals("typescript-bot")) {
          continue;
        }

        if (comment.getAuthor() != null) {
          authorCount.put(comment.getAuthor().getAuthorId(),
              authorCount.getOrDefault(comment.getAuthor().getAuthorId(), 0) + 1);

          if (nodes.stream().noneMatch(node ->
              node.getAuthorId().equals(comment.getAuthor().getAuthorId()))) {
            nodes.add(
                new Node(
                    comment.getAuthor().getAuthorId(),
                    comment.getAuthor().getAvatarUrl(),
                    0L
                )
            );
          }

          Node pullRequestNode = nodes.stream()
              .filter(filteredNode ->
                  filteredNode.getAuthorId().equals(pullRequest.getAuthor().getAuthorId()))
              .findFirst().get();

          // Increase the number of comments
          Node node = nodes.stream()
              .filter(filteredNode ->
                  filteredNode.getAuthorId().equals(comment.getAuthor().getAuthorId()))
              .findFirst().get();
          node.setSize(node.getSize() + 1);

          // Increase the number of links
          List<Author> authors = Arrays.asList(pullRequest.getAuthor(), comment.getAuthor());
          if (links.stream().noneMatch(link ->
              link.getSource().getAuthorId().equals(authors.getFirst().getAuthorId())
                  && link.getTarget().getAuthorId().equals(authors.get(1).getAuthorId()))) {
            links.add(
                new Link(
                    new Node(
                        authors.get(0).getAuthorId(),
                        authors.get(0).getAvatarUrl(),
                        pullRequestNode.getSize()
                    ),
                    new Node(
                        authors.get(1).getAuthorId(),
                        authors.get(1).getAvatarUrl(),
                        node.getSize()
                    ),
                    0L
                )
            );
          }

          Optional<Link> optionalLink = links.stream().filter(filteredLink ->
                  filteredLink.getSource().getAuthorId().equals(authors.getFirst()
                      .getAuthorId())
                      && filteredLink.getTarget().getAuthorId().equals(authors.get(1)
                      .getAuthorId())
              )
              .findFirst();

          if (optionalLink.isPresent()) {
            Link link = optionalLink.get();
            link.setThickness(link.getThickness() + 1);
          }
        }
      }
    }

    for (Node node : nodes) {
      String authorId = node.getAuthorId();
      int count = authorCount.get(authorId);
      node.setCount(count);
      double chosenColourValue = (double) count / durationInDays;
      node.setColourValue(chosenColourValue);
    }

    for (Link link : links) {
      String sourceAuthorId = link.getSource().getAuthorId();
      String targetAuthorId = link.getTarget().getAuthorId();
      int sourceCount = authorCount.get(sourceAuthorId);
      System.out.println("source count: " + sourceCount);
      int targetCount = authorCount.get(targetAuthorId);
      System.out.println("target count: " + targetCount);
      long averageCount = (sourceCount + targetCount) / 2;
      double chosenColourValue = (double) averageCount / durationInDays;
      System.out.println("chosen colour value: " + chosenColourValue);
      link.setColourValue((double) chosenColourValue);
    }

    authorCount.forEach((authorId, count) -> System.out.println(authorId + ": " + count));

    return new Graph(nodes, links, durationInDays);
  }
}
