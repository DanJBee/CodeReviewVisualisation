package danbee.frontend.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Graph controller class.
 *
 * @author Dan Bee
 */
@RestController
@RequestMapping
public class GraphController {

  /**
   * Index GET method.
   *
   * @return the index page
   */
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
  @GetMapping("/getGraph")
  public String getGraph(@RequestParam String owner,
                         @RequestParam String project,
                         @RequestParam(required = false) String start,
                         @RequestParam(required = false) String end) {
    if (start != null && end == null) {
      return "Hello "
          + owner
          + " you are the owner of project "
          + project
          + " at the start time of "
          + start
          + "!";
    } else if (start == null && end != null) {
      return "Hello "
          + owner
          + " you are the owner of project "
          + project
          + " at the end time of "
          + end
          + "!";
    } else if (start != null) {
      return "Hello "
          + owner
          + " you are the owner of project "
          + project
          + " at the start time of "
          + start
          + " and an end time of "
          + end
          + "!";
    }
    return "Hello " + owner + " you are the owner of project " + project + "!";
  }
}
