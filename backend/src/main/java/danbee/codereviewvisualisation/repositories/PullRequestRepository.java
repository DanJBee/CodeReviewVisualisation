package danbee.codereviewvisualisation.repositories;

import danbee.codereviewvisualisation.models.PullRequest;
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

  @Query(value = "SELECT * FROM pull_requests LIMIT 100;", nativeQuery = true)
  List<PullRequest> find100();

  PullRequest findByNumber(Long number);

  List<PullRequest> findByProjectId(Integer projectId);

  List<PullRequest> findByCreatedAt(Timestamp timestamp);
}
