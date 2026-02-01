package uk.ac.rhul.cs.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.rhul.cs.services.AnalysisService;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/gemini/")
public class AnalysisController {

    private final AnalysisService analysisService;

    public AnalysisController(AnalysisService analysisService) {
        this.analysisService = analysisService;
    }

    @GetMapping("/analyze/{projectId}")
    public String triggerAnalysis(@PathVariable Long projectId) {
        return analysisService.generateProjectReport(projectId);
    }
}
