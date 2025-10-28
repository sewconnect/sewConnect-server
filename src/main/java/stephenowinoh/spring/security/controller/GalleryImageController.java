package stephenowinoh.spring.security.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import stephenowinoh.spring.security.DTO.GalleryImageDTO;
import stephenowinoh.spring.security.service.GalleryImageService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/gallery-images")
@CrossOrigin(origins = "*")
public class GalleryImageController {

    @Autowired
    private GalleryImageService galleryImageService;

    @GetMapping("/gallery/{galleryId}")
    public ResponseEntity<List<GalleryImageDTO>> getImagesByGallery(@PathVariable Long galleryId) {
        List<GalleryImageDTO> images = galleryImageService.getImagesByGalleryId(galleryId);
        return ResponseEntity.ok(images);
    }

    @PostMapping("/gallery/{galleryId}")
    public ResponseEntity<?> addImage(
            @PathVariable Long galleryId,
            @RequestBody GalleryImageDTO imageDTO) {
        try {
            GalleryImageDTO created = galleryImageService.addImageToGallery(galleryId, imageDTO);
            return ResponseEntity.ok(created);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/{imageId}")
    public ResponseEntity<?> deleteImage(@PathVariable Long imageId) {
        try {
            galleryImageService.deleteImage(imageId);
            return ResponseEntity.ok(Map.of("message", "Image deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/gallery/{galleryId}/can-add")
    public ResponseEntity<Map<String, Boolean>> canAddImage(@PathVariable Long galleryId) {
        boolean canAdd = galleryImageService.canAddImage(galleryId);
        return ResponseEntity.ok(Map.of("canAdd", canAdd));
    }
}