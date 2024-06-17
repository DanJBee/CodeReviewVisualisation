package danbee.codereviewvisualisation.services;

import danbee.codereviewvisualisation.models.Project;
import org.springframework.stereotype.Service;

/**
 * Graph service class.
 *
 * @author Dan Bee
 */
@Service
public class GraphService {

  private final ProjectService projectService;

  public GraphService(ProjectService projectService) {
    this.projectService = projectService;
  }

  public Project getGraphData(String owner, String project) {
    return projectService.findByOwnerAndRepository(owner, project);
  }
}
