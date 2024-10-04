package uk.ac.rhul.cs.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ProjectTest {

  private Project project;

  @BeforeEach
  void setUp() {
    project = new Project("microsoft", "typescript");
  }

  @Test
  void testGetters() {
    assertNull(project.getId());
    assertEquals(project.getOwner(), "microsoft");
    assertEquals(project.getRepository(), "typescript");
  }

  @Test
  void testSetters() {
    project.setOwner("facebook");
    project.setRepository("react");

    assertNull(project.getId());
    assertEquals(project.getOwner(), "facebook");
    assertEquals(project.getRepository(), "react");
  }
}