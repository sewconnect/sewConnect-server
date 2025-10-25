package stephenowinoh.spring.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import stephenowinoh.spring.security.DTO.GalleryDTO;
import stephenowinoh.spring.security.DTO.ServiceDTO;
import stephenowinoh.spring.security.DTO.TailorDTO;
import stephenowinoh.spring.security.DTO.TailorProfileDTO;
import stephenowinoh.spring.security.mapper.TailorMapper;
import stephenowinoh.spring.security.model.MyUser;
import stephenowinoh.spring.security.repository.FollowRepository;
import stephenowinoh.spring.security.repository.MyUserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TailorService {

    @Autowired
    private MyUserRepository myUserRepository;

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private TailorServiceManager tailorServiceManager;

    @Autowired
    private GalleryService galleryService;

    /**
     * Get all tailors (for dashboard listing)
     */
    public List<TailorDTO> getAllTailors() {
        return myUserRepository.findByRole("TAILOR").stream()
                .map(TailorMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get tailors by specialty
     */
    public List<TailorDTO> getTailorsBySpecialty(String specialty) {
        return myUserRepository.findByRoleAndSpecialty("TAILOR", specialty).stream()
                .map(TailorMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get tailors by nationality
     */
    public List<TailorDTO> getTailorsByNationality(String nationality) {
        return myUserRepository.findByRoleAndNationality("TAILOR", nationality).stream()
                .map(TailorMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Search tailors by query
     */
    public List<TailorDTO> searchTailors(String query) {
        return myUserRepository.searchTailorsByQuery("TAILOR", query).stream()
                .map(TailorMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get simple tailor by ID (for listing)
     */
    public TailorDTO getTailorById(Long id) {
        return myUserRepository.findById(id)
                .filter(user -> "TAILOR".equals(user.getRole()))
                .map(TailorMapper::toDTO)
                .orElse(null);
    }

    /**
     * Get FULL tailor profile (with services, galleries, followers)
     * This is what we'll use for the profile page!
     */
    public TailorProfileDTO getTailorProfile(Long id) {
        MyUser tailor = myUserRepository.findById(id)
                .filter(user -> "TAILOR".equals(user.getRole()))
                .orElse(null);

        if (tailor == null) {
            return null;
        }

        TailorProfileDTO profile = new TailorProfileDTO();

        // Basic info
        profile.setId(tailor.getId());
        profile.setFullName(tailor.getFullName());
        profile.setUsername(tailor.getUsername());
        profile.setPhoneNumber(tailor.getPhoneNumber());
        profile.setSpecialty(tailor.getSpecialty());
        profile.setLocation(tailor.getLocation());
        profile.setNationality(tailor.getNationality());

        // Stats
        profile.setTotalFollowers((int) followRepository.countByFollowing(tailor));
        profile.setTotalFollowing((int) followRepository.countByFollower(tailor));
        profile.setTotalGalleries((int) galleryService.getGalleryCount(id));

        // Services
        List<ServiceDTO> services = tailorServiceManager.getServicesByTailorId(id);
        profile.setServices(services);

        // Galleries
        List<GalleryDTO> galleries = galleryService.getGalleriesByTailorId(id);
        profile.setGalleries(galleries);

        return profile;
    }

    /**
     * Get total tailor count
     */
    public long getTailorCount() {
        return myUserRepository.countByRole("TAILOR");
    }

    /**
     * Check if tailor exists
     */
    public boolean tailorExists(Long id) {
        return myUserRepository.findById(id)
                .map(user -> "TAILOR".equals(user.getRole()))
                .orElse(false);
    }
}