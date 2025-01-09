package uk.ac.rhul.cs.services;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import uk.ac.rhul.cs.models.Author;
import uk.ac.rhul.cs.models.Comment;
import uk.ac.rhul.cs.models.Graph;
import uk.ac.rhul.cs.models.Link;
import uk.ac.rhul.cs.models.Node;
import uk.ac.rhul.cs.models.PullRequest;

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

    long durationInDays = getDuration(startDate, endDate);

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
      // We excluded all bots that have typename with bot, but still some bots have user typename.
      if (pullRequest.getAuthor().getAuthorId().toLowerCase().contains("bot"))
        continue;

      authorCount.put(pullRequest.getAuthor().getAuthorId(),
          authorCount.getOrDefault(pullRequest.getAuthor().getAuthorId(), 0) + 1);

      Node prAuthor = findNodeOrCreate(nodes, pullRequest.getAuthor());

      for (Comment comment : pullRequest.getComments()) {
        if (comment.getAuthor() == null)
          continue;

        // Exclude some bots with user typename
        if (comment.getAuthor().getAuthorId().toLowerCase().contains("bot"))
          continue;

          authorCount.put(comment.getAuthor().getAuthorId(),
              authorCount.getOrDefault(comment.getAuthor().getAuthorId(), 0) + 1);

          Node commentAuthor = findNodeOrCreate(nodes, comment.getAuthor());
          commentAuthor.setSize(commentAuthor.getSize() + 1);
          commentAuthor.addCommentDate(comment.getDateString());

          List<Node> authors = Arrays.asList(prAuthor, commentAuthor);
          authors.sort(Comparator.comparing(Node::getAuthorId));

          Link link;
          if (links.stream().noneMatch(
              l -> l.getSource().equals(authors.get(0)) && l.getTarget().equals(authors.get(1)))) {
            link = new Link(authors.get(0), authors.get(1), 0l);
            links.add(link);
          } else {
            link = links.stream().filter(
                l -> l.getSource().equals(authors.get(0)) && l.getTarget().equals(authors.get(1)))
                .findFirst().get();
          }
          link.setThickness(link.getThickness() + 1);
          link.addCommentDate(comment.getDateString());
      }
    }

    for (Node node : nodes) {
      node.setColourValue((double) node.getNumberOfCommentDates() / durationInDays);
    }
    for (Link link : links) {
      link.setColourValue((double) link.getNumberOfCommentDates() / durationInDays);
    }

    authorCount.forEach((authorId, count) -> System.out.println(authorId + ": " + count));

    return new Graph(nodes, links, durationInDays);
  }

  private long getDuration(LocalDateTime startDate, LocalDateTime endDate) {
    Duration duration = Duration.between(startDate, endDate);
    long durationInDays = duration.toDays();

    return durationInDays - (durationInDays / 7 * 2); // exclude weekend
  }


  private Node findNodeOrCreate(List<Node> nodes, Author author) {
    if (nodes.stream()
        .noneMatch(n -> n.getAuthorId().equals(author.getAuthorId()))) {
      Node tempNode = new Node(author.getAuthorId(), author.getAuthorAvatarUrl(), 0l);
      nodes.add(tempNode);
      return tempNode;
    } else {
      return nodes.stream().filter(n -> n.getAuthorId().equals(author.getAuthorId())).findFirst()
          .get();
    }
  }
}
