package uk.ac.rhul.cs.services;

import uk.ac.rhul.cs.models.Project;
import uk.ac.rhul.cs.repositories.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

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
    assertEquals("{\"owner\":\"microsoft\",\"repository\":\"typescript\"}",
        projectService.findByOwnerAndRepository("microsoft", "typescript").toString());
  }

  @Test
  void testFindByOwnerAndRepositoryInvalid() {
    assertNull(projectService.findByOwnerAndRepository("invalidOwner", "invalidRepository"));
  }
}