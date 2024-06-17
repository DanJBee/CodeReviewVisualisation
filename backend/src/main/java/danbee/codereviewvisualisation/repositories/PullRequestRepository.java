package danbee.codereviewvisualisation.repositories;

import danbee.codereviewvisualisation.models.PullRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Timestamp;
import java.util.List;

/**
 * Pull request repository class.
 *
 * @author Dan Bee
 */
public interface PullRequestRepository extends JpaRepository<PullRequest, Long> {

  PullRequest findByNumber(Long number);

  List<PullRequest> findByProjectId(Integer projectId);

  List<PullRequest> findByCreatedAt(Timestamp timestamp);

  List<PullRequest> findByAuthorId(String authorId);
}
