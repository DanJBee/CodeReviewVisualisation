package danbee.codereviewvisualisation.services;

import danbee.codereviewvisualisation.models.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Project repository class.
 *
 * @author Dan Bee
 */
public interface ProjectRepository extends JpaRepository<Project, Long> {

  Project findByOwner(String owner);

  List<Project> findByRepository(String repository);
}
