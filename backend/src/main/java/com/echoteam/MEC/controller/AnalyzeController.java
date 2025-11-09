
package com.echoteam.MEC.controller;
import com.echoteam.MEC.dto.AnalysisRequest;
import com.echoteam.MEC.dto.AnalysisResponse;
import com.echoteam.MEC.service.AnalysisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/analyze", produces = MediaType.APPLICATION_JSON_VALUE)
public class AnalyzeController {

    private static final Logger log = LoggerFactory.getLogger(AnalyzeController.class);
    private final AnalysisService analysisService;

    public AnalyzeController(AnalysisService analysisService) {
        this.analysisService = analysisService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AnalysisResponse> analyze(
            @RequestBody AnalysisRequest req,
            @RequestParam(value = "provider", required = false) String provider,
            @RequestHeader(value = "X-LLM-Provider", required = false) String providerHeader) {

        AnalysisResponse result = analysisService.analyze(req, provider, providerHeader);
        return ResponseEntity.ok(result);
    }

    @GetMapping(path = "/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("Bad request: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntime(RuntimeException ex) {
        log.error("Analysis error", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Analysis failed: " + ex.getMessage());
    }
}
