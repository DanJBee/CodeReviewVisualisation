package uk.ac.rhul.cs.services;

import uk.ac.rhul.cs.models.PullRequest;
import uk.ac.rhul.cs.repositories.PullRequestRepository;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

/**
 * Pull request service class.
 *
 * @author Dan Bee
 */
@Service
public class PullRequestService {

  private final PullRequestRepository pullRequestRepository;

  public PullRequestService(PullRequestRepository pullRequestRepository) {
    this.pullRequestRepository = pullRequestRepository;
  }

  public List<PullRequest> findPullRequestsByProjectId(Timestamp start,
                                                       Timestamp end,
                                                       Integer id) {
    return pullRequestRepository.findPullRequests(start, end, id);
  }
}
