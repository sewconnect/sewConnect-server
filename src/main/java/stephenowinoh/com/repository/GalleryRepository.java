package stephenowinoh.com.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stephenowinoh.com.model.Gallery;
import stephenowinoh.com.model.MyUser;

import java.util.List;

@Repository
public interface GalleryRepository extends JpaRepository<Gallery, Long> {

    // Find all active galleries by tailor
    List<Gallery> findByTailorAndIsActiveTrueOrderByCreatedAtDesc(MyUser tailor);

    // Find galleries by tailor ID
    List<Gallery> findByTailorIdAndIsActiveTrueOrderByCreatedAtDesc(Long tailorId);

    // Count active galleries for a tailor (to enforce 12 max limit)
    long countByTailorAndIsActiveTrue(MyUser tailor);

    // Count galleries by tailor ID
    long countByTailorIdAndIsActiveTrue(Long tailorId);

    // Check if tailor can create more galleries (max 12)
    default boolean canCreateGallery(MyUser tailor) {
        return countByTailorAndIsActiveTrue(tailor) < 12;
    }
}