package stephenowinoh.spring.security.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import stephenowinoh.spring.security.DTO.TailorDTO;
import stephenowinoh.spring.security.DTO.TailorProfileDTO;
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
     * Get all tailors (for dashboard listing)
     */
    @GetMapping
    public ResponseEntity<List<TailorDTO>> getAllTailors() {
        List<TailorDTO> tailors = tailorService.getAllTailors();
        return ResponseEntity.ok(tailors);
    }

    /**
     * GET /api/tailors/{id}
     * Get simple tailor info by ID
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
     * GET /api/tailors/{id}/profile
     * Get FULL tailor profile (with services, galleries, followers)
     * THIS IS THE NEW ENDPOINT FOR THE PROFILE PAGE!
     */
    @GetMapping("/{id}/profile")
    public ResponseEntity<?> getTailorProfile(@PathVariable Long id) {
        TailorProfileDTO profile = tailorService.getTailorProfile(id);

        if (profile == null) {
            return ResponseEntity
                    .status(404)
                    .body(Map.of("message", "Tailor not found"));
        }

        return ResponseEntity.ok(profile);
    }

    /**
     * GET /api/tailors/specialty/{specialty}
     * Filter tailors by specialty
     */
    @GetMapping("/specialty/{specialty}")
    public ResponseEntity<List<TailorDTO>> getTailorsBySpecialty(@PathVariable String specialty) {
        List<TailorDTO> tailors = tailorService.getTailorsBySpecialty(specialty);
        return ResponseEntity.ok(tailors);
    }

    /**
     * GET /api/tailors/nationality/{nationality}
     * Filter tailors by nationality
     */
    @GetMapping("/nationality/{nationality}")
    public ResponseEntity<List<TailorDTO>> getTailorsByNationality(@PathVariable String nationality) {
        List<TailorDTO> tailors = tailorService.getTailorsByNationality(nationality);
        return ResponseEntity.ok(tailors);
    }

    /**
     * GET /api/tailors/search?query=Jane
     * Search tailors
     */
    @GetMapping("/search")
    public ResponseEntity<List<TailorDTO>> searchTailors(@RequestParam String query) {
        List<TailorDTO> tailors = tailorService.searchTailors(query);
        return ResponseEntity.ok(tailors);
    }

    /**
     * GET /api/tailors/count
     * Get total number of tailors
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