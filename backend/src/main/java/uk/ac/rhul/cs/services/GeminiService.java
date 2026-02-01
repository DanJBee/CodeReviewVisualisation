package uk.ac.rhul.cs.services;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.util.*;

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate;

    public GeminiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String analyzeReviewQuality(String promptText) {
	// Create the request before sending it to Gemini
	String urlWithKey = apiUrl + "?key=" + apiKey;

	HttpHeaders headers = new HttpHeaders();
	headers.setContentType(MediaType.APPLICATION_JSON);
	
	// Model the requets according to Gemini rules
	Map<String, Object> textPart = Map.of("text", promptText);
	Map<String, Object> contentsPart = Map.of("parts", List.of(textPart));

	Map<String, Object> requestBody = Map.of(
	   "contents", List.of(contentsPart),
	   "generationConfig", Map.of(
        	"response_mime_type", "application/json"
    	   )
	);

	HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

	try {
	   ResponseEntity<String> response = restTemplate.postForEntity(urlWithKey, entity, String.class);
	   return response.getBody();
	} catch (Exception e) {
	   return "AI Analysis failed: " + e.getMessage();
	}
    }
}
