package uk.ac.rhul.cs.repositories;

import uk.ac.rhul.cs.models.Author;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Author repository class.
 *
 * @author Dan Bee
 */
public interface AuthorRepository extends JpaRepository<Author, String> {
}
