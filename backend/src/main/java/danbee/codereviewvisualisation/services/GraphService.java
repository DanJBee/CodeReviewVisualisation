package danbee.codereviewvisualisation.services;

import danbee.codereviewvisualisation.models.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Graph service class.
 *
 * @author Dan Bee
 */
@Service
public class GraphService {

  private final PullRequestService pullRequestService;

  private final AuthorService authorService;

  private final CommentService commentService;

  /**
   * Graph service controller method.
   */
  public GraphService(PullRequestService pullRequestService, AuthorService authorService, CommentService commentService) {
    this.pullRequestService = pullRequestService;
    this.authorService = authorService;
    this.commentService = commentService;
  }

  /**
   * getGraphData method.
   *
   * @param owner   the owner of the project
   * @param project the project name
   * @return the graph data
   */
  public Graph getGraphData(String owner, String project) {
    List<PullRequest> pullRequests = pullRequestService.find100();
    List<Node> nodes = new ArrayList<>();
    List<Link> links = new ArrayList<>();
    for (PullRequest pullRequest : pullRequests) {
      if (nodes.stream().noneMatch(node -> node.getAuthorId().equals(pullRequest.getAuthor().getAuthorId()))) {
        nodes.add(
            new Node(
                pullRequest.getAuthor().getAuthorId(),
                pullRequest.getAuthor().getAvatarUrl(),
                pullRequest.getAuthor().getTypeName(),
                0L
            )
        );
      }

      for (Comment comment : pullRequest.getComments()) {
        if (nodes.stream().noneMatch(node -> node.getAuthorId().equals(comment.getAuthor().getAuthorId()))) {
          nodes.add(
              new Node(
                  comment.getAuthor().getAuthorId(),
                  comment.getAuthor().getAvatarUrl(),
                  comment.getAuthor().getTypeName(),
                  0L
              )
          );
        }

        // Increase the number of comments
        Node node = nodes.stream()
            .filter(filteredNode ->
                filteredNode.getAuthorId().equals(comment.getAuthor().getAuthorId()))
            .findFirst().get();
        node.setSize(node.getSize() + 1);

        // Increase the number of links
        List<Author> authors = Arrays.asList(pullRequest.getAuthor(), comment.getAuthor());
        if (links.stream().noneMatch(link ->
            link.getSource().equals(authors.getFirst())
                && link.getTarget().equals(authors.get(1)))) {
          links.add(
              new Link(
                  new Node(
                      authors.get(0).getAuthorId(),
                      authors.get(0).getAvatarUrl(),
                      authors.get(0).getTypeName(),
                      0L
                  ),
                  new Node(
                      authors.get(1).getAuthorId(),
                      authors.get(1).getAvatarUrl(),
                      authors.get(1).getTypeName(),
                      0L
                  ),
                  0L
              )
          );
        }

//        Link link = links.stream().filter(filteredLink ->
//            filteredLink.getSource().equals(authors.get(0))
//                && filteredLink.getTarget().equals(authors.get(1))).findFirst().get();
//        link.setThickness(link.getThickness() + 1);
      }
    }

    return new Graph(nodes, links);
  }
}
