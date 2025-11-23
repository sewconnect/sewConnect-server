package stephenowinoh.com.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import stephenowinoh.com.DTO.GalleryDTO;
import stephenowinoh.com.service.GalleryService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/galleries")
@CrossOrigin(origins = "*")
public class GalleryController {

    @Autowired
    private GalleryService galleryService;

    /**
     * GET /api/galleries/{galleryId}
     * Get a single gallery by ID
     */
    @GetMapping("/{galleryId}")
    public ResponseEntity<?> getGalleryById(@PathVariable Long galleryId) {
        try {
            GalleryDTO gallery = galleryService.getGalleryById(galleryId);
            return ResponseEntity.ok(gallery);
        } catch (RuntimeException e) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * GET /api/galleries/tailor/{tailorId}
     * Get all galleries for a specific tailor
     */
    @GetMapping("/tailor/{tailorId}")
    public ResponseEntity<List<GalleryDTO>> getGalleriesByTailor(@PathVariable Long tailorId) {
        List<GalleryDTO> galleries = galleryService.getGalleriesByTailorId(tailorId);
        return ResponseEntity.ok(galleries);
    }

    /**
     * GET /api/galleries/tailor/{tailorId}/count
     * Get gallery count for a tailor
     */
    @GetMapping("/tailor/{tailorId}/count")
    public ResponseEntity<Map<String, Long>> getGalleryCount(@PathVariable Long tailorId) {
        long count = galleryService.getGalleryCount(tailorId);
        return ResponseEntity.ok(Map.of("count", count));
    }

    /**
     * GET /api/galleries/tailor/{tailorId}/can-create
     * Check if tailor can create more galleries
     */
    @GetMapping("/tailor/{tailorId}/can-create")
    public ResponseEntity<Map<String, Boolean>> canCreateGallery(@PathVariable Long tailorId) {
        boolean canCreate = galleryService.canCreateGallery(tailorId);
        return ResponseEntity.ok(Map.of("canCreate", canCreate));
    }

    /**
     * POST /api/galleries/tailor/{tailorId}
     * Create a new gallery for a tailor
     */
    @PostMapping("/tailor/{tailorId}")
    public ResponseEntity<?> createGallery(
            @PathVariable Long tailorId,
            @RequestBody GalleryDTO galleryDTO) {
        try {
            GalleryDTO created = galleryService.createGallery(tailorId, galleryDTO);
            return ResponseEntity.ok(created);
        } catch (RuntimeException e) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * PUT /api/galleries/{galleryId}
     * Update an existing gallery
     */
    @PutMapping("/{galleryId}")
    public ResponseEntity<?> updateGallery(
            @PathVariable Long galleryId,
            @RequestBody GalleryDTO galleryDTO) {
        try {
            GalleryDTO updated = galleryService.updateGallery(galleryId, galleryDTO);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * DELETE /api/galleries/{galleryId}
     * Delete a gallery (soft delete)
     */
    @DeleteMapping("/{galleryId}")
    public ResponseEntity<?> deleteGallery(@PathVariable Long galleryId) {
        try {
            galleryService.deleteGallery(galleryId);
            return ResponseEntity.ok(Map.of("message", "Gallery deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }
}