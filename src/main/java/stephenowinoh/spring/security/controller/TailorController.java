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

    @GetMapping
    public ResponseEntity<List<TailorDTO>> getAllTailors() {
        List<TailorDTO> tailors = tailorService.getAllTailors();
        return ResponseEntity.ok(tailors);
    }

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

    @GetMapping("/specialty/{specialty}")
    public ResponseEntity<List<TailorDTO>> getTailorsBySpecialty(@PathVariable String specialty) {
        List<TailorDTO> tailors = tailorService.getTailorsBySpecialty(specialty);
        return ResponseEntity.ok(tailors);
    }

    @GetMapping("/nationality/{nationality}")
    public ResponseEntity<List<TailorDTO>> getTailorsByNationality(@PathVariable String nationality) {
        List<TailorDTO> tailors = tailorService.getTailorsByNationality(nationality);
        return ResponseEntity.ok(tailors);
    }

    @GetMapping("/search")
    public ResponseEntity<List<TailorDTO>> searchTailors(@RequestParam String query) {
        List<TailorDTO> tailors = tailorService.searchTailors(query);
        return ResponseEntity.ok(tailors);
    }

    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> getTailorCount() {
        long count = tailorService.getTailorCount();
        return ResponseEntity.ok(Map.of("count", count));
    }

    @GetMapping("/exists/{id}")
    public ResponseEntity<Map<String, Boolean>> tailorExists(@PathVariable Long id) {
        boolean exists = tailorService.tailorExists(id);
        return ResponseEntity.ok(Map.of("exists", exists));
    }
}