package uk.ac.rhul.cs.repositories;

import uk.ac.rhul.cs.models.PullRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.sql.Timestamp;
import java.util.List;

/**
 * Pull request repository class.
 *
 * @author Dan Bee
 */
public interface PullRequestRepository extends JpaRepository<PullRequest, Long> {

  @Query(value = "SELECT * FROM pull_requests WHERE created_at BETWEEN :start AND :end "
      + "AND project_id = :id;",
      nativeQuery = true)
  List<PullRequest> findPullRequests(Timestamp start, Timestamp end, Integer id);

  PullRequest findByNumber(Long number);

  List<PullRequest> findByProjectId(Integer projectId);

  List<PullRequest> findByCreatedAt(Timestamp timestamp);
}
