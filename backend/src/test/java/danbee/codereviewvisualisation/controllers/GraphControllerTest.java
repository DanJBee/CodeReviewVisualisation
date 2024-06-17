package danbee.codereviewvisualisation.controllers;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Graph controller test class.
 *
 * @author Dan Bee
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GraphControllerTest {

  @Autowired
  private TestRestTemplate restTemplate;

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
  void testGetGetGraphWithoutStartAndEndTime() {
    ResponseEntity<String> response = restTemplate
        .getForEntity("/getGraph?owner=microsoft&project=typescript", String.class);
    assertThat(response.getStatusCode())
        .isEqualTo(HttpStatus.OK);
    DocumentContext documentContext = JsonPath.parse(response.getBody());
    Integer id = documentContext.read("$[0].id");
    assertThat(id)
        .isEqualTo(1079);
    Integer number = documentContext.read("$[0].number");
    assertThat(number)
        .isEqualTo(1000);
    Integer projectId = documentContext.read("$[0].projectId");
    assertThat(projectId)
        .isEqualTo(11);
    String createdAt = documentContext.read("$[0].createdAt");
    assertThat(createdAt)
        .isEqualTo("2014-10-30T15:15:03.000+00:00");
    String authorId = documentContext.read("$[0].authorId");
    assertThat(authorId)
        .isEqualTo("jrieken");
  }

  @Test
  @Disabled
  void testGetGetGraphWithBothStartAndEndTime() {
    ResponseEntity<String> response = restTemplate
        .getForEntity(
            "/getGraph?owner=microsoft&project=typescript&start=2024-05-21T15:16:07.257Z"
                + "&end=2024-05-21T15:16:07.257Z", String.class);
    assertThat(response.getStatusCode())
        .isEqualTo(HttpStatus.OK);
    assertThat(response.getBody())
        .isEqualTo(
            "Hello microsoft you are the owner of project typescript at the start"
                + " time of 2024-05-21T15:16:07.257Z and an end time of 2024-05-21T15:16:07.257Z!");
  }

  @Test
  @Disabled
  void testGetGetGraphWithOnlyStartTime() {
    ResponseEntity<String> response = restTemplate
        .getForEntity(
            "/getGraph?owner=microsoft&project=typescript&start=2024-05-21T15:16:07.257Z",
            String.class);
    assertThat(response.getStatusCode())
        .isEqualTo(HttpStatus.OK);
    assertThat(response.getBody())
        .isEqualTo(
            "Hello microsoft you are the owner of project typescript at the start"
                + " time of 2024-05-21T15:16:07.257Z!");
  }

  @Test
  @Disabled
  void testGetGetGraphWithOnlyEndTime() {
    ResponseEntity<String> response = restTemplate
        .getForEntity(
            "/getGraph?owner=microsoft&project=typescript&end=2024-05-21T15:16:07.257Z",
            String.class);
    assertThat(response.getStatusCode())
        .isEqualTo(HttpStatus.OK);
    assertThat(response.getBody())
        .isEqualTo(
            "Hello microsoft you are the owner of project typescript at the end"
                + " time of 2024-05-21T15:16:07.257Z!");
  }
}