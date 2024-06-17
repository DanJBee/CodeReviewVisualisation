package danbee.codereviewvisualisation.services;

import danbee.codereviewvisualisation.models.PullRequest;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Graph service class.
 *
 * @author Dan Bee
 */
@Service
public class GraphService {

  private final ProjectService projectService;

  private final PullRequestService pullRequestService;

  public GraphService(ProjectService projectService, PullRequestService pullRequestService) {
    this.projectService = projectService;
    this.pullRequestService = pullRequestService;
  }

  public List<PullRequest> getGraphData(String owner, String project) {
    Integer projectId = projectService.findByOwnerAndRepository(owner, project).getId();
    return pullRequestService.findByProjectId(projectId);
  }
}
