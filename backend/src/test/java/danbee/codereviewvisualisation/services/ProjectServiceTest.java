package danbee.codereviewvisualisation.services;

import danbee.codereviewvisualisation.models.Project;
import danbee.codereviewvisualisation.repositories.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
@Transactional
class ProjectServiceTest {

  @Autowired
  private ProjectService projectService;

  @Autowired
  private ProjectRepository projectRepository;

  @BeforeEach
  void beforeEach() {
    projectRepository.deleteAllAndResetAutoIncrement();

    Project project = new Project("microsoft", "typescript");
    projectRepository.save(project);
  }

  @Test
  void testFindByOwnerAndRepositoryValid() {
    assertEquals("{\"id\":1,\"owner\":\"microsoft\",\"repository\":\"typescript\"}",
        projectService.findByOwnerAndRepository("microsoft", "typescript").toString());
  }

  @Test
  void testFindByOwnerAndRepositoryInvalid() {
    assertNull(projectService.findByOwnerAndRepository("invalidOwner", "invalidRepository"));
  }

  @Test
  void testFindByOwnerValid() {
    assertEquals("microsoft", projectService.findByOwner("microsoft").getOwner());
  }

  @Test
  void testFindByOwnerInvalid() {
    assertNull(projectService.findByOwner("invalidOwner"));
  }

  @Test
  void testFindByRepositoryValid() {
    assertEquals("typescript", projectService
        .findByRepository("typescript").getFirst().getRepository());
  }

  @Test
  void testFindByRepositoryInvalid() {
    List<Project> projects = new ArrayList<>();
    assertEquals(projects, projectService.findByRepository("invalidRepository"));
  }
}