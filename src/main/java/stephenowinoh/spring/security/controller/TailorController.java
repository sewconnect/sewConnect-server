package stephenowinoh.spring.security.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import stephenowinoh.spring.security.DTO.TailorDTO;
import stephenowinoh.spring.security.service.TailorService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tailors")
@CrossOrigin(origins = "*")
public class TailorController {

    @Autowired
    private TailorService tailorService;

    /**
     * GET /api/tailors
     * Get all tailors
     */
    @GetMapping
    public ResponseEntity<List<TailorDTO>> getAllTailors() {
        List<TailorDTO> tailors = tailorService.getAllTailors();
        return ResponseEntity.ok(tailors);
    }

    /**
     * GET /api/tailors/specialty/{specialty}
     * Filter tailors by specialty
     * Example: /api/tailors/specialty/Wedding & Evening Wear Specialist
     */
    @GetMapping("/specialty/{specialty}")
    public ResponseEntity<List<TailorDTO>> getTailorsBySpecialty(@PathVariable String specialty) {
        List<TailorDTO> tailors = tailorService.getTailorsBySpecialty(specialty);
        return ResponseEntity.ok(tailors);
    }

    /**
     * GET /api/tailors/nationality/{nationality}
     * Filter tailors by nationality
     * Example: /api/tailors/nationality/Kenyan
     */
    @GetMapping("/nationality/{nationality}")
    public ResponseEntity<List<TailorDTO>> getTailorsByNationality(@PathVariable String nationality) {
        List<TailorDTO> tailors = tailorService.getTailorsByNationality(nationality);
        return ResponseEntity.ok(tailors);
    }

    /**
     * GET /api/tailors/search?query=Jane
     * Search tailors by name, location, or nationality
     */
    @GetMapping("/search")
    public ResponseEntity<List<TailorDTO>> searchTailors(@RequestParam String query) {
        List<TailorDTO> tailors = tailorService.searchTailors(query);
        return ResponseEntity.ok(tailors);
    }

    /**
     * GET /api/tailors/{id}
     * Get single tailor by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getTailorById(@PathVariable Long id) {
        TailorDTO tailor = tailorService.getTailorById(id);

        if (tailor == null) {
            return ResponseEntity
                    .status(404)
                    .body(Map.of("message", "Tailor not found"));
        }

        return ResponseEntity.ok(tailor);
    }

    /**
     * GET /api/tailors/count
     * Get total number of tailors (for statistics)
     */
    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> getTailorCount() {
        long count = tailorService.getTailorCount();
        return ResponseEntity.ok(Map.of("count", count));
    }

    /**
     * GET /api/tailors/exists/{id}
     * Check if tailor exists
     */
    @GetMapping("/exists/{id}")
    public ResponseEntity<Map<String, Boolean>> tailorExists(@PathVariable Long id) {
        boolean exists = tailorService.tailorExists(id);
        return ResponseEntity.ok(Map.of("exists", exists));
    }
}