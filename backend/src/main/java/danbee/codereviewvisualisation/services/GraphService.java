package danbee.codereviewvisualisation.services;

import danbee.codereviewvisualisation.models.*;
import org.springframework.stereotype.Service;

import java.util.*;

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
    List<PullRequest> pullRequests = pullRequestService.find100();
    Map<String, Node> nodes = new HashMap<>();
    Map<String, Link> links = new HashMap<>();

    for (PullRequest pullRequest : pullRequests) {
      if (!nodes.containsKey(pullRequest.getAuthor().getAuthorId())) {
        nodes.put(pullRequest.getAuthor().getAuthorId(),
            new Node(
                pullRequest.getAuthor().getAuthorId(),
                pullRequest.getAuthor().getAvatarUrl(),
                pullRequest.getAuthor().getTypeName(),
                0L)
        );

        for (Comment comment : pullRequest.getComments()) {
          if (!nodes.containsKey(comment.getAuthor().getAuthorId())) {
            nodes.put(comment.getAuthor().getAuthorId(),
                new Node(
                    comment.getAuthor().getAuthorId(),
                    comment.getAuthor().getAvatarUrl(),
                    comment.getAuthor().getTypeName(),
                    0L));
          }

          // Increase the number of comments for the node with the given author ID
          Node node = nodes.get(comment.getAuthor().getAuthorId());
          node.setSize(node.getSize() + 1);

          final String splitter = ":::";

          // Increase the number of links for the node with the given author ID
          List<String> sortedAuthors = Arrays.asList(
              pullRequest.getAuthor().getAuthorId(),
              comment.getAuthor().getAuthorId()
          );
          Collections.sort(sortedAuthors);
          String pair = String.join(splitter, sortedAuthors);
          if (!links.containsKey(pair)) {
            links.put(pair, new Link(
                new Node(
                    sortedAuthors.get(0),
                    pullRequest.getAuthor().getAvatarUrl(),
                    pullRequest.getAuthor().getTypeName(),
                    0L
                ),
                new Node(
                    sortedAuthors.get(1),
                    comment.getAuthor().getAvatarUrl(),
                    comment.getAuthor().getTypeName(),
                    0L
                ),
                0));
          }
          Link link = links.get(pair);
          link.setThickness(link.getThickness() + 1);
        }
      }
    }
//    List<Author> authors = authorService.findAll();
//    for (Author author : authors) {
//      Long size = commentService.findCountOfAuthorId(author.getAuthorId());
//      nodes.add(new Node(author.getAuthorId(), author.getAvatarUrl(), author.getTypeName(), size));
//    }
//    List<Link> links = new ArrayList<>();
    // TODO: Fix links
    //    List<Comment> comments = commentService.findAll();
    //    for (Comment comment : comments) {
    //      String avatarUrl = commentService
    //          .findAvatarUrlByAuthorId(comment.getAuthorId());
    //      String typeName = commentService
    //          .findTypeNameByAuthorId(comment.getAuthorId());
    //      Long size = commentService.findCountOfAuthorId(comment.getAuthorId());
    //      Node sourceNode = new Node(comment.getAuthorId(),
    //          avatarUrl,
    //          typeName,
    //          size);
    //      // Temporary value until the contents of the target node are worked out
    //      links.add(new Link(sourceNode, sourceNode, 1));
    //    }
    return new Graph(nodes, links);
  }
}
