package danbee.codereviewvisualisation.repositories;

import danbee.codereviewvisualisation.models.Author;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Author repository class.
 *
 * @author Dan Bee
 */
public interface AuthorRepository extends JpaRepository<Author, String> {
}
