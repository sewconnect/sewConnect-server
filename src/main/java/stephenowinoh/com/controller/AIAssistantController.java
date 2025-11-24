package stephenowinoh.com.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*")
public class AIAssistantController {

    @Value("${groq.api.key:}")  // Default to empty string if not set
    private String groqApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Check if AI service is available
     */
    private boolean isAIServiceAvailable() {
        return groqApiKey != null
                && !groqApiKey.isEmpty()
                && !groqApiKey.equals("test_groq_key")
                && !groqApiKey.equals("test_key");
    }

    @PostMapping("/chat")
    public ResponseEntity<?> chat(@RequestBody Map<String, Object> request) {
        // Check if AI service is configured
        if (!isAIServiceAvailable()) {
            log.warn("AI service not configured - GROQ_API_KEY missing or invalid");
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of(
                            "error", "AI service is not configured",
                            "message", "Please contact support or try again later",
                            "available", false
                    ));
        }

        try {
            String groqUrl = "https://api.groq.com/openai/v1/chat/completions";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(groqApiKey);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    groqUrl,
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            return ResponseEntity.ok(response.getBody());

        } catch (Exception e) {
            log.error("AI service error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "error", "AI service temporarily unavailable",
                            "message", "Please try again later",
                            "available", true  // Service is configured but failing
                    ));
        }
    }

    /**
     * Health check endpoint for AI service
     */
    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        boolean available = isAIServiceAvailable();

        return ResponseEntity.ok(Map.of(
                "available", available,
                "message", available
                        ? "AI service is configured and ready"
                        : "AI service is not configured"
        ));
    }
}
