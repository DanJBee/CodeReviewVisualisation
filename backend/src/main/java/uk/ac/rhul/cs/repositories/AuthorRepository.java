package uk.ac.rhul.cs.repositories;

import uk.ac.rhul.cs.models.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Author repository class.
 *
 * @author Dan Bee
 */
public interface AuthorRepository extends JpaRepository<Author, String> {

  @Query("SELECT a.authorAvatarUrl FROM Author a WHERE LOWER(a.authorId) = LOWER(:username)")
  String findAvatarUrlByUsername(@Param("username") String username);
}
