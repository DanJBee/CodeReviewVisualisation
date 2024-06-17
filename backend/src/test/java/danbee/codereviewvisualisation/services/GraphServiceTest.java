package danbee.codereviewvisualisation.services;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
class GraphServiceTest {

  @Autowired
  private GraphService graphService;

  @Test
  @Disabled
  void testGetGraphValid() {
  }

  @Test
  @Disabled
  void testGraphInvalid() {
  }
}