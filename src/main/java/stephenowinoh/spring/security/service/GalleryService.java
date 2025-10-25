package stephenowinoh.spring.security.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stephenowinoh.spring.security.DTO.GalleryDTO;
import stephenowinoh.spring.security.model.Gallery;
import stephenowinoh.spring.security.model.MyUser;
import stephenowinoh.spring.security.repository.GalleryRepository;
import stephenowinoh.spring.security.repository.MyUserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GalleryService {

    @Autowired
    private GalleryRepository galleryRepository;

    @Autowired
    private MyUserRepository myUserRepository;

    private static final int MAX_GALLERIES_PER_TAILOR = 12;

    /**
     * Get all galleries for a tailor
     */
    public List<GalleryDTO> getGalleriesByTailorId(Long tailorId) {
        return galleryRepository.findByTailorIdAndIsActiveTrueOrderByCreatedAtDesc(tailorId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Create a new gallery for a tailor
     */
    @Transactional
    public GalleryDTO createGallery(Long tailorId, GalleryDTO galleryDTO) {
        MyUser tailor = myUserRepository.findById(tailorId)
                .orElseThrow(() -> new RuntimeException("Tailor not found"));

        if (!"TAILOR".equals(tailor.getRole())) {
            throw new RuntimeException("User is not a tailor");
        }

        // Check if tailor has reached the 12 gallery limit
        long galleryCount = galleryRepository.countByTailorIdAndIsActiveTrue(tailorId);
        if (galleryCount >= MAX_GALLERIES_PER_TAILOR) {
            throw new RuntimeException("Maximum gallery limit reached (12 galleries)");
        }

        Gallery gallery = new Gallery();
        gallery.setTailor(tailor);
        gallery.setGalleryName(galleryDTO.getGalleryName());
        gallery.setDescription(galleryDTO.getDescription());
        gallery.setImageCount(0);
        gallery.setIsActive(true);

        Gallery saved = galleryRepository.save(gallery);
        return toDTO(saved);
    }

    /**
     * Update an existing gallery
     */
    @Transactional
    public GalleryDTO updateGallery(Long galleryId, GalleryDTO galleryDTO) {
        Gallery gallery = galleryRepository.findById(galleryId)
                .orElseThrow(() -> new RuntimeException("Gallery not found"));

        gallery.setGalleryName(galleryDTO.getGalleryName());
        gallery.setDescription(galleryDTO.getDescription());

        if (galleryDTO.getThumbnailUrl() != null) {
            gallery.setThumbnailUrl(galleryDTO.getThumbnailUrl());
        }

        Gallery updated = galleryRepository.save(gallery);
        return toDTO(updated);
    }

    /**
     * Delete a gallery (soft delete)
     */
    @Transactional
    public void deleteGallery(Long galleryId) {
        Gallery gallery = galleryRepository.findById(galleryId)
                .orElseThrow(() -> new RuntimeException("Gallery not found"));

        gallery.setIsActive(false);
        galleryRepository.save(gallery);
    }

    /**
     * Get gallery count for a tailor
     */
    public long getGalleryCount(Long tailorId) {
        return galleryRepository.countByTailorIdAndIsActiveTrue(tailorId);
    }

    /**
     * Check if tailor can create more galleries
     */
    public boolean canCreateGallery(Long tailorId) {
        return galleryRepository.countByTailorIdAndIsActiveTrue(tailorId) < MAX_GALLERIES_PER_TAILOR;
    }

    /**
     * Convert Gallery entity to DTO
     */
    private GalleryDTO toDTO(Gallery gallery) {
        return new GalleryDTO(
                gallery.getId(),
                gallery.getGalleryName(),
                gallery.getDescription(),
                gallery.getImageCount(),
                gallery.getThumbnailUrl(),
                gallery.getCreatedAt(),
                gallery.getUpdatedAt()
        );
    }
}
