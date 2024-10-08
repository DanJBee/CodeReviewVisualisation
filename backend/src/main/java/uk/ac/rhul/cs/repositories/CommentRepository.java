package uk.ac.rhul.cs.repositories;

import uk.ac.rhul.cs.models.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.sql.Timestamp;
import java.util.List;

/**
 * Comment repository class.
 *
 * @author Dan Bee
 */
public interface CommentRepository extends JpaRepository<Comment, Long> {

  List<Comment> findByCreatedAt(Timestamp timestamp);

  @Query(value = "SELECT COUNT(author_id) FROM comments WHERE author_id = :authorId;",
      nativeQuery = true)
  Long findCountOfAuthorId(String authorId);

  @Query(value = "SELECT author_avatar_url FROM authors, comments WHERE authors.author_id"
      + "= comments.author_id AND authors.author_id = :authorId;", nativeQuery = true)
  List<String> findAvatarUrlByAuthorId(String authorId);

  @Query(value = "SELECT type_name FROM authors, comments WHERE authors.author_id"
      + "= comments.author_id AND  authors.author_id = :authorId;", nativeQuery = true)
  List<String> findTypeNameByAuthorId(String authorId);
}
