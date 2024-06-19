package danbee.codereviewvisualisation.services;

import danbee.codereviewvisualisation.models.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Graph service class.
 *
 * @author Dan Bee
 */
@Service
public class GraphService {

  private final PullRequestService pullRequestService;

  /**
   * Graph service controller method.
   */
  public GraphService(PullRequestService pullRequestService) {
    this.pullRequestService = pullRequestService;
  }

  /**
   * getGraphData method.
   *
   * @param owner   the owner of the project
   * @param project the project name
   * @return the graph data
   */
  public Graph getGraphData(String owner, String project) {
    List<PullRequest> pullRequests = pullRequestService.findAll();
    List<Node> nodes = new ArrayList<>();
    List<Link> links = new ArrayList<>();
    for (PullRequest pullRequest : pullRequests) {
      if (pullRequest.getAuthor().getAuthorId().equals("typescript-bot")) {
        continue;
      }

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

    return new Graph(nodes, links);
  }
}
