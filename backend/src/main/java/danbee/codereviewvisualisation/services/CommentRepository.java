package danbee.codereviewvisualisation.services;

import danbee.codereviewvisualisation.models.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Timestamp;
import java.util.List;

/**
 * Comment repository class.
 *
 * @author Dan Bee
 */
public interface CommentRepository extends JpaRepository<Comment, Long> {

  List<Comment> findByNumber(Long number);

  List<Comment> findByAuthorId(String authorId);

  List<Comment> findByCreatedAt(Timestamp timestamp);
}
