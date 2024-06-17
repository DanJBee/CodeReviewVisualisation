package danbee.codereviewvisualisation.services;

import danbee.codereviewvisualisation.models.Author;
import danbee.codereviewvisualisation.models.Graph;
import danbee.codereviewvisualisation.models.Link;
import danbee.codereviewvisualisation.models.Node;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Graph service class.
 *
 * @author Dan Bee
 */
@Service
public class GraphService {

  private final AuthorService authorService;

  private final CommentService commentService;

  /**
   * Graph service controller method.
   *
   * @param authorService the author service
   */
  public GraphService(AuthorService authorService, CommentService commentService) {
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
    List<Node> nodes = new ArrayList<>();
    List<Author> authors = authorService.findAll();
    for (Author author : authors) {
      Long size = commentService.findCountOfAuthorId(author.getAuthorId());
      nodes.add(new Node(author.getAuthorId(), author.getAvatarUrl(), author.getTypeName(), size));
    }
    List<Link> links = new ArrayList<>();
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
