package stephenowinoh.spring.security.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import stephenowinoh.spring.security.model.GalleryImage;

import java.util.List;

public interface GalleryImageRepository extends JpaRepository<GalleryImage, Long> {
    List<GalleryImage> findByGalleryIdOrderByUploadedAtDesc(Long galleryId);
    long countByGalleryId(Long galleryId);
}