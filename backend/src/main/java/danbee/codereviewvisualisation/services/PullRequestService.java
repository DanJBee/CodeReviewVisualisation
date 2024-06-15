package danbee.codereviewvisualisation.services;

import danbee.codereviewvisualisation.models.PullRequest;
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

  public PullRequest findByNumber(Long number) {
    return pullRequestRepository.findByNumber(number);
  }

  public List<PullRequest> findByProjectId(Integer projectId) {
    return pullRequestRepository.findByProjectId(projectId);
  }

  public List<PullRequest> findByCreatedAt(Timestamp timestamp) {
    return pullRequestRepository.findByCreatedAt(timestamp);
  }

  public List<PullRequest> findByAuthorId(String authorId) {
    return pullRequestRepository.findByAuthorId(authorId);
  }
}
