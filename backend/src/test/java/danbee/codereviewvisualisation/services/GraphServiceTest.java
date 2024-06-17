package danbee.codereviewvisualisation.services;

import danbee.codereviewvisualisation.models.PullRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
class GraphServiceTest {

  @Autowired
  private GraphService graphService;

  @Test
  void testGetGraphValid() {
    List<PullRequest> pullRequests = graphService.getGraphData("microsoft", "typescript");
    PullRequest firstPullRequest = pullRequests.getFirst();
    Long id = firstPullRequest.getId();
    assertThat(id)
        .isEqualTo(1079);
    Long number = firstPullRequest.getNumber();
    assertThat(number)
        .isEqualTo(1000);
    Integer projectId = firstPullRequest.getProjectId();
    assertThat(projectId)
        .isEqualTo(11);
    Timestamp createdAt = firstPullRequest.getCreatedAt();
    assertThat(createdAt)
        .isEqualTo(Timestamp.valueOf("2014-10-30 15:15:03.0"));
    String authorId = firstPullRequest.getAuthorId();
    assertThat(authorId)
        .isEqualTo("jrieken");
  }

  @Test
  void testGraphInvalid() {
    assertThrows(NullPointerException.class, () ->
        graphService.getGraphData("invalidOwner", "invalidProject"));
  }
}