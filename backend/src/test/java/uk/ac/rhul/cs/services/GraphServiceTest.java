package uk.ac.rhul.cs.services;

import uk.ac.rhul.cs.models.*;
import uk.ac.rhul.cs.repositories.CommentRepository;
import uk.ac.rhul.cs.repositories.ProjectRepository;
import uk.ac.rhul.cs.repositories.PullRequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Graph service test class.
 *
 * @author Dan Bee
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
public class GraphServiceTest {

  Instant now = Instant.now().plusSeconds(3600);
  ZonedDateTime zonedDateTime = now.atZone(ZoneId.of("UTC"));
  String time = zonedDateTime
      .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SS'Z'"));
  Timestamp formattedTime = Timestamp.from(Instant.parse(time));

  @Autowired
  private GraphService graphService;

  @Autowired
  private ProjectRepository projectRepository;

  @Autowired
  private PullRequestRepository pullRequestRepository;

  @Autowired
  private CommentRepository commentRepository;

  @BeforeEach
  void beforeEach() {
    projectRepository.deleteAllAndResetAutoIncrement();

    Project project = new Project("microsoft", "typescript");
    projectRepository.save(project);

    pullRequestRepository.deleteAll();

    commentRepository.deleteAll();

    Timestamp timestamp = Timestamp.from(zonedDateTime.toInstant());
    ZonedDateTime timestampZonedDateTime = timestamp.toInstant().atZone(ZoneId.of("UTC"));
    Timestamp formattedTimestamp = Timestamp.valueOf(timestampZonedDateTime.format(
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SS")
    ));

    Author author = new Author("author", "avatar_url");
    List<Comment> comments = new ArrayList<>();

    PullRequest pullRequest = new PullRequest(1000L, 1, formattedTimestamp, comments);
    pullRequest.setAuthor(author);
    // Make the comment creation date 30 minutes after the pull request creation date
    Comment comment = new Comment(Timestamp.from(formattedTimestamp.toInstant()
        .plus(30, ChronoUnit.MINUTES)), pullRequest);
    comment.setAuthor(author);
    comments.add(comment);
    pullRequestRepository.save(pullRequest);
    commentRepository.save(comment);
  }

  @Test
  void testGetGraphValidWithBothStartAndEndTime() {
    // Subtract an hour from the formatted time string for the start time
    Timestamp startTime = new Timestamp(formattedTime.getTime() - 3600000);

    // Add an hour to the formatted time string for the end time
    Timestamp endTime = new Timestamp(formattedTime.getTime() + 3600000);

    String start = startTime.toInstant().toString();
    String end = endTime.toInstant().toString();

    Graph graph = graphService.getGraphData(
        "microsoft",
        "typescript",
        start,
        end);
    assertEquals(graph.getNodes().getFirst().getAuthorId(), "author");
    assertEquals(graph.getNodes().getFirst().getAvatarUrl(), "avatar_url");
    assertEquals(1L, graph.getNodes().getFirst().getSize());
    assertEquals(1, graph.getLinks().size());
    assertEquals("author", graph.getLinks().getFirst().getSource().getAuthorId());
    assertEquals("author", graph.getLinks().getFirst().getTarget().getAuthorId());
  }

  @Test
  void testGetGraphValidWithOnlyStartTime() {
    // Subtract an hour from the formatted time string for the start time
    Timestamp startTime = new Timestamp(formattedTime.getTime() - 3600000);

    String start = startTime.toInstant().toString();

    Graph graph = graphService.getGraphData(
        "microsoft",
        "typescript",
        start,
        null);
    assertEquals(graph.getNodes().getFirst().getAuthorId(), "author");
    assertEquals(graph.getNodes().getFirst().getAvatarUrl(), "avatar_url");
    assertEquals(1L, graph.getNodes().getFirst().getSize());
    assertEquals(1, graph.getLinks().size());
    assertEquals("author", graph.getLinks().getFirst().getSource().getAuthorId());
    assertEquals("author", graph.getLinks().getFirst().getTarget().getAuthorId());
  }

  @Test
  void testGetGraphValidWithOnlyEndTime() {
    // Add an hour to the formatted time string for the end time
    Timestamp endTime = new Timestamp(formattedTime.getTime() + 3600000);

    String end = endTime.toInstant().toString();

    Graph graph = graphService.getGraphData(
        "microsoft",
        "typescript",
        null,
        end);
    assertEquals(graph.getNodes().getFirst().getAuthorId(), "author");
    assertEquals(graph.getNodes().getFirst().getAvatarUrl(), "avatar_url");
    assertEquals(1L, graph.getNodes().getFirst().getSize());
    assertEquals(1, graph.getLinks().size());
    assertEquals("author", graph.getLinks().getFirst().getSource().getAuthorId());
    assertEquals("author", graph.getLinks().getFirst().getTarget().getAuthorId());
  }

  @Test
  void testGraphInvalid() {
    assertThrows(NullPointerException.class, () -> graphService.getGraphData(
        "invalidOwner",
        "invalidProject",
        null,
        null));
  }

  @Test
  void testTypeScriptBot() {
    pullRequestRepository.deleteAll();
    commentRepository.deleteAll();

    Timestamp timestamp = Timestamp.from(zonedDateTime.toInstant());
    ZonedDateTime timestampZonedDateTime = timestamp.toInstant().atZone(ZoneId.of("UTC"));
    Timestamp formattedTimestamp = Timestamp.valueOf(timestampZonedDateTime.format(
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SS")
    ));

    Author author = new Author("typescript-bot", "avatar_url");
    List<Comment> comments = new ArrayList<>();

    PullRequest pullRequest = new PullRequest(1000L, 1, formattedTimestamp, comments);
    pullRequest.setAuthor(author);
    // Make the comment creation date 30 minutes after the pull request creation date
    Comment comment = new Comment(Timestamp.from(formattedTimestamp.toInstant()
        .plus(30, ChronoUnit.MINUTES)), pullRequest);
    comment.setAuthor(author);
    comments.add(comment);
    pullRequestRepository.save(pullRequest);
    commentRepository.save(comment);

    // Subtract an hour from the formatted time string for the start time
    Timestamp startTime = new Timestamp(formattedTime.getTime() - 3600000);

    // Add an hour to the formatted time string for the end time
    Timestamp endTime = new Timestamp(formattedTime.getTime() + 3600000);

    String start = startTime.toInstant().toString();
    String end = endTime.toInstant().toString();

    Graph graph = graphService.getGraphData(
        "microsoft",
        "typescript",
        start,
        end);

    assertEquals(0, graph.getNodes().size());
    assertEquals(0, graph.getLinks().size());
  }

  @Test
  void testNodePrOrCommentAlreadyExisting() {
    pullRequestRepository.deleteAll();
    commentRepository.deleteAll();

    Timestamp timestamp = Timestamp.from(zonedDateTime.toInstant());
    ZonedDateTime timestampZonedDateTime = timestamp.toInstant().atZone(ZoneId.of("UTC"));
    Timestamp formattedTimestamp = Timestamp.valueOf(timestampZonedDateTime.format(
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SS")
    ));

    Author author = new Author("author", "avatar_url");
    Author author2 = new Author("author2", "another_avatar_url");
    List<Comment> comments = new ArrayList<>();

    PullRequest pullRequest = new PullRequest(1000L, 1, formattedTimestamp, comments);
    pullRequest.setAuthor(author);
    // Make the comment creation date 30 minutes after the pull request creation date
    Comment comment = new Comment(Timestamp.from(formattedTimestamp.toInstant()
        .plus(30, ChronoUnit.MINUTES)), pullRequest);
    comment.setAuthor(author2);
    comments.add(comment);
    pullRequestRepository.save(pullRequest);
    commentRepository.save(comment);

    // Subtract an hour from the formatted time string for the start time
    Timestamp startTime = new Timestamp(formattedTime.getTime() - 3600000);

    // Add an hour to the formatted time string for the end time
    Timestamp endTime = new Timestamp(formattedTime.getTime() + 3600000);

    String start = startTime.toInstant().toString();
    String end = endTime.toInstant().toString();

    Graph graph = graphService.getGraphData(
        "microsoft",
        "typescript",
        start,
        end);
    assertEquals(graph.getNodes().getFirst().getAuthorId(), "author");
    assertEquals(graph.getNodes().getFirst().getAvatarUrl(), "avatar_url");
    assertEquals(2, graph.getNodes().size());
    assertEquals(1, graph.getLinks().size());
    assertEquals("author", graph.getLinks().getFirst().getSource().getAuthorId());
    assertEquals("author2", graph.getLinks().getFirst().getTarget().getAuthorId());
  }
}