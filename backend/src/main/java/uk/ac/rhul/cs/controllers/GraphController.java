package uk.ac.rhul.cs.controllers;

import uk.ac.rhul.cs.models.Graph;
import uk.ac.rhul.cs.models.Node;
import uk.ac.rhul.cs.services.GraphService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Graph controller class.
 *
 * @author Dan Bee
 */
@RestController
@RequestMapping
public class GraphController {

  private final GraphService graphService;

  public GraphController(GraphService graphService) {
    this.graphService = graphService;
  }

  /**
   * Index GET method.
   *
   * @return the index page
   */
  @CrossOrigin(origins = "http://localhost:5173")
  @GetMapping
  public String index() {
    return "Welcome to the Code Review Visualisation!";
  }

  /**
   * getGraph GET method.
   *
   * @param owner   the owner of the repository
   * @param project the repository name
   * @param start   the starting date of the chosen time window (optional)
   * @param end     the ending date of the chosen time window (optional)
   * @return the pull request graph information for 'microsoft/typescript'
   */
  @GetMapping("/getGraph/{owner}/{project}")
  public Graph getGraph(@PathVariable String owner,
                        @PathVariable String project,
                        @RequestParam(required = false) String start,
                        @RequestParam(required = false) String end) {
    Graph graph = graphService.getGraphData(owner, project, start, end);
    List<Node> nodes = graph.getNodes();
    long maxSize = nodes.stream()
        .mapToLong(Node::getSize)
        .max()
        .orElse(0L);
    for (Node node : nodes) {
      node.setSize(Math.max(20, (long) (Math.log(node.getSize()) / Math.log(maxSize) * 100)));
    }
    return graph;
  }
}
