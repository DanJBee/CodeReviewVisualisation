package uk.ac.rhul.cs.services;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

import uk.ac.rhul.cs.services.GeminiService;
import uk.ac.rhul.cs.repositories.CommentRepository;
import uk.ac.rhul.cs.models.Comment;
import uk.ac.rhul.cs.dto.DeveloperReportDTO;
import uk.ac.rhul.cs.repositories.AuthorRepository;

@Service
public class AnalysisService {

    private final CommentRepository commentRepository;
    private final GeminiService geminiService;
	
    private final AuthorRepository authorRepository;
    private final ObjectMapper objectMapper;

    public AnalysisService(CommentRepository commentRepository, GeminiService geminiService,
		    AuthorRepository authorRepository, ObjectMapper objectMapper) {
        this.commentRepository = commentRepository;
        this.geminiService = geminiService;
	this.authorRepository = authorRepository;
	this.objectMapper = objectMapper;
    }

    private List<DeveloperReportDTO> addAvatarURLToAIResponse(String aiResponse) {
	try {
	  JsonNode rootNode = objectMapper.readTree(aiResponse);

	  String rawAiText = rootNode.path("candidates").get(0)
				.path("content")
 			  	.path("parts").get(0)
	  	  		.path("text").asText();
	  // Remove markdown backticks if they exist
	  String cleanJson = rawAiText.replace("```json", "").replace("```", "").trim();
	  // Parse JSON string into a List of Data Transfer Objects
	  List<DeveloperReportDTO> reports = objectMapper.readValue(
			  cleanJson, new TypeReference<List<DeveloperReportDTO>>() {}
	  );
	  // Add Avatar URLs from db
	  List<DeveloperReportDTO> enrichedReports = reports.stream()
		  .map(report -> {
			  String cleanUsername = report.username().trim();
			  // Query the avatar URL of the author fron the database given the author name
			  String dbAvatar = authorRepository.findAvatarUrlByUsername(cleanUsername);

			  return new DeveloperReportDTO(
					  cleanUsername,
					  dbAvatar,
					  report.valueScore(),
					  report.workVolume(),
					  report.advice(),
					  report.isSlacker()
			  );
		  }).collect(Collectors.toList());
	  return enrichedReports;

	} catch (JsonProcessingException e) {
	  throw new RuntimeException("Failed to parse AI response", e);
	}
    }

    public List<DeveloperReportDTO> generateProjectReport(Long projectId) {
        List<Comment> allComments = commentRepository.findAllByProjectId(projectId);
       	
       	// Group all the comments of each author in a list, keyed by author username	
        Map<String, List<Comment>> userActivity = allComments.stream()
            .collect(Collectors.groupingBy(c -> c.getAuthor().getAuthorId()));
	
	// Create AI Persona and give it its task
        StringBuilder prompt = new StringBuilder();
        prompt.append("Act as a Senior Tech Lead. Analyze the following developer activities. ");
        prompt.append("For each user, assess their contribution value, work volume, give advice, and flag them if they are a 'slacker' (someone providing low-value, generic comments like 'nice' or 'fixed'). Also, analyze the git diff and git patch urls, and assess if the comments are relevant to the code contribution. Make sure to slip a few jokes about their performance to lighten up the mood, but do not exagerate. Be funny!\n\n");

        userActivity.forEach((username, comments) -> {
            prompt.append("Developer: ").append(username).append("\n");
            for (Comment c : comments) {
                prompt.append("Comment: ").append(c.getCommentBody()).append("\n");
                prompt.append("PR Diff: ").append(c.getPullRequest().getDiffUrl()).append("\n");
                prompt.append("Patch URL: ").append(c.getPullRequest().getPatchUrl()).append("\n");
            }
            prompt.append("\n");
        });

        prompt.append("\nReturn the result as a JSON array of objects with keys: ");
        prompt.append("[username, valueScore(1-10), workVolume(High/Med/Low), advice, isSlacker(boolean)].");

	String aiResponse = geminiService.analyzeReviewQuality(prompt.toString());
        return addAvatarURLToAIResponse(aiResponse); 
    }
}
