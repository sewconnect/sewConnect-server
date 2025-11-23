package stephenowinoh.com.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import stephenowinoh.com.DTO.TailorDTO;
import stephenowinoh.com.DTO.TailorProfileDTO;
import stephenowinoh.com.DTO.ProfileUpdateRequest;
import stephenowinoh.com.service.TailorService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tailors")
@CrossOrigin(origins = {
        "http://localhost:5173",
        "https://sewconnectplatform.vercel.app"
})
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

    // ========== PROFILE PICTURE MANAGEMENT ==========

    /**
     * Upload/Update profile picture
     */
    @PutMapping("/{id}/profile-picture")
    public ResponseEntity<?> updateProfilePicture(
            @PathVariable Long id,
            @RequestBody Map<String, String> request,
            Authentication authentication) {

        try {
            // Verify user owns this profile
            if (!tailorService.isOwner(id, authentication)) {
                return ResponseEntity
                        .status(403)
                        .body(Map.of("message", "Unauthorized"));
            }

            String profilePictureUrl = request.get("profilePictureUrl");
            String publicId = request.get("publicId");

            if (profilePictureUrl == null || profilePictureUrl.trim().isEmpty()) {
                return ResponseEntity
                        .badRequest()
                        .body(Map.of("message", "Profile picture URL is required"));
            }

            boolean updated = tailorService.updateProfilePicture(id, profilePictureUrl, publicId);

            if (!updated) {
                return ResponseEntity
                        .status(404)
                        .body(Map.of("message", "Tailor not found"));
            }

            return ResponseEntity.ok(Map.of(
                    "message", "Profile picture updated successfully",
                    "profilePictureUrl", profilePictureUrl
            ));

        } catch (Exception e) {
            return ResponseEntity
                    .status(500)
                    .body(Map.of("message", "Failed to update profile picture: " + e.getMessage()));
        }
    }

    /**
     * Delete profile picture
     */
    @DeleteMapping("/{id}/profile-picture")
    public ResponseEntity<?> deleteProfilePicture(
            @PathVariable Long id,
            Authentication authentication) {

        try {
            // Verify user owns this profile
            if (!tailorService.isOwner(id, authentication)) {
                return ResponseEntity
                        .status(403)
                        .body(Map.of("message", "Unauthorized"));
            }

            boolean deleted = tailorService.deleteProfilePicture(id);

            if (!deleted) {
                return ResponseEntity
                        .status(404)
                        .body(Map.of("message", "Tailor not found"));
            }

            return ResponseEntity.ok(Map.of(
                    "message", "Profile picture deleted successfully"
            ));

        } catch (Exception e) {
            return ResponseEntity
                    .status(500)
                    .body(Map.of("message", "Failed to delete profile picture: " + e.getMessage()));
        }
    }

    // ========== PROFILE INFORMATION UPDATE ==========

    /**
     * Update tailor profile information
     */
    @PutMapping("/{id}/profile")
    public ResponseEntity<?> updateProfile(
            @PathVariable Long id,
            @RequestBody ProfileUpdateRequest request,
            Authentication authentication) {

        try {
            // Verify user owns this profile
            if (!tailorService.isOwner(id, authentication)) {
                return ResponseEntity
                        .status(403)
                        .body(Map.of("message", "Unauthorized"));
            }

            TailorProfileDTO updated = tailorService.updateProfile(id, request);

            if (updated == null) {
                return ResponseEntity
                        .status(404)
                        .body(Map.of("message", "Tailor not found"));
            }

            return ResponseEntity.ok(updated);

        } catch (Exception e) {
            return ResponseEntity
                    .status(500)
                    .body(Map.of("message", "Failed to update profile: " + e.getMessage()));
        }
    }

    /**
     * Add/Update specialty (comma-separated for multiple)
     */
    @PutMapping("/{id}/specialty")
    public ResponseEntity<?> updateSpecialty(
            @PathVariable Long id,
            @RequestBody Map<String, String> request,
            Authentication authentication) {

        try {
            // Verify user owns this profile
            if (!tailorService.isOwner(id, authentication)) {
                return ResponseEntity
                        .status(403)
                        .body(Map.of("message", "Unauthorized"));
            }

            String specialty = request.get("specialty");

            if (specialty == null || specialty.trim().isEmpty()) {
                return ResponseEntity
                        .badRequest()
                        .body(Map.of("message", "Specialty is required"));
            }

            boolean updated = tailorService.updateSpecialty(id, specialty);

            if (!updated) {
                return ResponseEntity
                        .status(404)
                        .body(Map.of("message", "Tailor not found"));
            }

            return ResponseEntity.ok(Map.of(
                    "message", "Specialty updated successfully",
                    "specialty", specialty
            ));

        } catch (Exception e) {
            return ResponseEntity
                    .status(500)
                    .body(Map.of("message", "Failed to update specialty: " + e.getMessage()));
        }
    }

    // ========== EXISTING ENDPOINTS ==========

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