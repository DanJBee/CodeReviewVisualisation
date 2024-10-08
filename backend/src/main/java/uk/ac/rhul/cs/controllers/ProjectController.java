package uk.ac.rhul.cs.controllers;

import com.google.gson.Gson;
import uk.ac.rhul.cs.models.Project;
import uk.ac.rhul.cs.repositories.ProjectRepository;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class ProjectController {
  private ProjectRepository projectRepository;

  public ProjectController(ProjectRepository projectRepository) {
    this.projectRepository = projectRepository;
  }

  @GetMapping("/projects")
  public ResponseEntity<String> getProjects() {
    List<Project> projects = projectRepository.findAll();

    Gson gson = new Gson();
    return ResponseEntity.ok(gson.toJson(projects));
  }
}
