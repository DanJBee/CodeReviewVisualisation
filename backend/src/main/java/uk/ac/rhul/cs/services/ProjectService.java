package uk.ac.rhul.cs.services;

import uk.ac.rhul.cs.models.Project;
import uk.ac.rhul.cs.repositories.ProjectRepository;
import org.springframework.stereotype.Service;

/**
 * Project service class.
 *
 * @author Dan Bee
 */
@Service
public class ProjectService {

  private final ProjectRepository projectRepository;

  public ProjectService(ProjectRepository projectRepository) {
    this.projectRepository = projectRepository;
  }

  public Project findByOwnerAndRepository(String owner, String repository) {
    return projectRepository.findByOwnerAndRepository(owner, repository);
  }
}
