package danbee.codereviewvisualisation.services;

import danbee.codereviewvisualisation.models.Project;
import danbee.codereviewvisualisation.repositories.ProjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;

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

  public Project findByOwner(String owner) {
    return projectRepository.findByOwner(owner);
  }

  public List<Project> findByRepository(String repository) {
    return projectRepository.findByRepository(repository);
  }
}
