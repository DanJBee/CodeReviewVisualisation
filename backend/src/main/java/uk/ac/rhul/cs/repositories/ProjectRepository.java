package uk.ac.rhul.cs.repositories;

import uk.ac.rhul.cs.models.Project;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * Project repository class.
 *
 * @author Dan Bee
 */
public interface ProjectRepository extends JpaRepository<Project, Long> {

  Project findByOwnerAndRepository(String owner, String project);

  Project findByOwner(String owner);

  List<Project> findByRepository(String repository);

  // Added for tests!
  // Method to delete all records and reset auto-increment in MySQL
  @Modifying
  @Query(value = "TRUNCATE TABLE projects", nativeQuery = true)
  void deleteAllAndResetAutoIncrement();
}
