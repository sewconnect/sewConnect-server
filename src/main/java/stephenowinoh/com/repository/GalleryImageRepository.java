package stephenowinoh.com.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import stephenowinoh.com.model.GalleryImage;

import java.util.List;

public interface GalleryImageRepository extends JpaRepository<GalleryImage, Long> {
    List<GalleryImage> findByGalleryIdOrderByUploadedAtDesc(Long galleryId);
    long countByGalleryId(Long galleryId);
}