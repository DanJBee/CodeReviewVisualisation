package uk.ac.rhul.cs.services;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;

import uk.ac.rhul.cs.services.GeminiService;
import uk.ac.rhul.cs.repositories.CommentRepository;
import uk.ac.rhul.cs.models.Comment;

@Service
public class AnalysisService {

    private final CommentRepository commentRepository;
    private final GeminiService geminiService;

    public AnalysisService(CommentRepository commentRepository, GeminiService geminiService) {
        this.commentRepository = commentRepository;
        this.geminiService = geminiService;
    }

    public String generateProjectReport(Long projectId) {
        List<Comment> allComments = commentRepository.findAllByProjectId(projectId);
       	
       	// Group all the comments of each author in a list, keyed by author username	
        Map<String, List<Comment>> userActivity = allComments.stream()
            .collect(Collectors.groupingBy(c -> c.getAuthor().getAuthorId()));
	
	// Create AI Persona and give it its task
        StringBuilder prompt = new StringBuilder();
        prompt.append("Act as a Senior Tech Lead. Analyze the following developer activities. ");
        prompt.append("For each user, assess their contribution value, work volume, give advice, and flag them if they are a 'slacker' (someone providing low-value, generic comments like 'nice' or 'fixed'). Also, analyze the git diff and git patch urls, and assess if the comments are relevant to the code contribution. Make sure to slip a few jokes about their performance to lighten up the mood, but do not exagerate.\n\n");

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

        return geminiService.analyzeReviewQuality(prompt.toString());
    }
}
