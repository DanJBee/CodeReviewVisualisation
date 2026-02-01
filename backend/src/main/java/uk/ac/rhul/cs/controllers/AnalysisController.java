package uk.ac.rhul.cs.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import java.util.List;

import uk.ac.rhul.cs.services.AnalysisService;
import uk.ac.rhul.cs.dto.DeveloperReportDTO;

@RestController
@RequestMapping("/gemini/")
@CrossOrigin(origins = "http://localhost:5173")
public class AnalysisController {

    private final AnalysisService analysisService;

    public AnalysisController(AnalysisService analysisService) {
        this.analysisService = analysisService;
    }

    @GetMapping("/analyze/{projectId}")
    public List<DeveloperReportDTO> triggerAnalysis(@PathVariable Long projectId) {
        return analysisService.generateProjectReport(projectId);
    }
}
