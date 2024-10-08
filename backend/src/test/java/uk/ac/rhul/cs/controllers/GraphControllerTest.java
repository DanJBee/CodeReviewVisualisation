package uk.ac.rhul.cs.controllers;

import uk.ac.rhul.cs.models.*;
import uk.ac.rhul.cs.repositories.ProjectRepository;
import uk.ac.rhul.cs.repositories.PullRequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Graph controller test class.
 *
 * @author Dan Bee
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
class GraphControllerTest {

  @Autowired
  private TestRestTemplate restTemplate;

  @Autowired
  private ProjectRepository projectRepository;

  @Autowired
  private PullRequestRepository pullRequestRepository;
  @Autowired
  private GraphController graphController;

  @BeforeEach
  void beforeEach() {
    projectRepository.deleteAllAndResetAutoIncrement();

    Project project = new Project("microsoft", "typescript");
    projectRepository.save(project);

    pullRequestRepository.deleteAll();

    Instant now = Instant.now().plusSeconds(3600);
    ZonedDateTime zonedDateTime = now.atZone(ZoneId.of("UTC"));

    Timestamp timestamp = Timestamp.from(zonedDateTime.toInstant());
    ZonedDateTime timestampZonedDateTime = timestamp.toInstant().atZone(ZoneId.of("UTC"));
    Timestamp formattedTimestamp = Timestamp.valueOf(timestampZonedDateTime.format(
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SS")
    ));

    Author author = new Author("author", "avatar_url");
    PullRequest pullRequest;
    List<Comment> comments = new ArrayList<>();
    // Make the comment creation date an hour after the pull request creation date
    Comment comment = new Comment(Timestamp.from(formattedTimestamp.toInstant()
        .plus(1, ChronoUnit.HOURS)), null);
    comment.setAuthor(author);
    comments.add(comment);
    pullRequest = new PullRequest(1000L, 1, formattedTimestamp, comments);
    pullRequest.setAuthor(author);
    comment.setPullRequest(pullRequest);
    pullRequestRepository.save(pullRequest);
  }

  @Test
  void testGetIndex() {
    ResponseEntity<String> response = restTemplate
        .getForEntity("/", String.class);
    assertThat(response.getStatusCode())
        .isEqualTo(HttpStatus.OK);
    assertThat(response.getBody())
        .isEqualTo("Welcome to the Code Review Visualisation!");
  }

  @Test
  void testGetGetGraphValid() {
    Graph graph = graphController.getGraph("microsoft", "typescript", null, null);
    assertThat(graph.getNodes().size())
        .isEqualTo(1);
    assertThat(graph.getLinks().size())
        .isEqualTo(1);
  }

  @Test
  void testGetGetGraphInvalid() {
    assertThrows(NullPointerException.class, () ->
        graphController.getGraph("invalidOwner", "invalidProject", null, null));
  }
}