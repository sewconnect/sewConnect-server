package stephenowinoh.com.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import stephenowinoh.com.DTO.GalleryDTO;
import stephenowinoh.com.DTO.ServiceDTO;
import stephenowinoh.com.DTO.TailorDTO;
import stephenowinoh.com.DTO.TailorProfileDTO;
import stephenowinoh.com.DTO.ProfileUpdateRequest;
import stephenowinoh.com.mapper.TailorMapper;
import stephenowinoh.com.model.MyUser;
import stephenowinoh.com.repository.FollowRepository;
import stephenowinoh.com.repository.MyUserRepository;

import java.util.List;
import java.util.Map;
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

    @Autowired
    private Cloudinary cloudinary;

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
        profile.setProfilePictureUrl(tailor.getProfilePictureUrl());
        profile.setProfilePicturePublicId(tailor.getProfilePicturePublicId());
        profile.setBio(tailor.getBio());

        // Stats
        profile.setTotalFollowers(followRepository.countByTailor(tailor).intValue());
        profile.setTotalFollowing(followRepository.countByFollower(tailor).intValue());
        profile.setTotalGalleries((int) galleryService.getGalleryCount(id));

        // Services
        List<ServiceDTO> services = tailorServiceManager.getServicesByTailorId(id);
        profile.setServices(services);

        // Galleries
        List<GalleryDTO> galleries = galleryService.getGalleriesByTailorId(id);
        profile.setGalleries(galleries);

        return profile;
    }

    // ========== PROFILE MANAGEMENT METHODS ==========

    /**
     * Update profile picture
     */
    public boolean updateProfilePicture(Long id, String profilePictureUrl, String publicId) {
        return myUserRepository.findById(id)
                .filter(user -> "TAILOR".equals(user.getRole()))
                .map(user -> {
                    // Delete old profile picture from Cloudinary if exists
                    if (user.getProfilePicturePublicId() != null && !user.getProfilePicturePublicId().isEmpty()) {
                        try {
                            cloudinary.uploader().destroy(user.getProfilePicturePublicId(), ObjectUtils.emptyMap());
                        } catch (Exception e) {
                            System.err.println("Failed to delete old profile picture: " + e.getMessage());
                            // Continue anyway - we still want to update with new picture
                        }
                    }

                    user.setProfilePictureUrl(profilePictureUrl);
                    user.setProfilePicturePublicId(publicId);
                    myUserRepository.save(user);
                    return true;
                })
                .orElse(false);
    }

    /**
     * Delete profile picture (also deletes from Cloudinary)
     */
    public boolean deleteProfilePicture(Long id) {
        return myUserRepository.findById(id)
                .filter(user -> "TAILOR".equals(user.getRole()))
                .map(user -> {
                    String publicId = user.getProfilePicturePublicId();

                    // Delete from Cloudinary if publicId exists
                    if (publicId != null && !publicId.isEmpty()) {
                        try {
                            Map result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
                            System.out.println("Cloudinary delete result: " + result.get("result"));
                        } catch (Exception e) {
                            System.err.println("Failed to delete from Cloudinary: " + e.getMessage());
                            // Continue anyway - we still want to remove from database
                        }
                    }

                    // Clear from database
                    user.setProfilePictureUrl(null);
                    user.setProfilePicturePublicId(null);
                    myUserRepository.save(user);
                    return true;
                })
                .orElse(false);
    }

    /**
     * Update profile information
     */
    public TailorProfileDTO updateProfile(Long id, ProfileUpdateRequest request) {
        MyUser tailor = myUserRepository.findById(id)
                .filter(user -> "TAILOR".equals(user.getRole()))
                .orElse(null);

        if (tailor == null) {
            return null;
        }

        // Update fields if provided
        if (request.getFullName() != null && !request.getFullName().trim().isEmpty()) {
            tailor.setFullName(request.getFullName().trim());
        }
        if (request.getPhoneNumber() != null && !request.getPhoneNumber().trim().isEmpty()) {
            tailor.setPhoneNumber(request.getPhoneNumber().trim());
        }
        if (request.getLocation() != null && !request.getLocation().trim().isEmpty()) {
            tailor.setLocation(request.getLocation().trim());
        }
        if (request.getSpecialty() != null && !request.getSpecialty().trim().isEmpty()) {
            tailor.setSpecialty(request.getSpecialty().trim());
        }
        if (request.getNationality() != null && !request.getNationality().trim().isEmpty()) {
            tailor.setNationality(request.getNationality().trim());
        }
        if (request.getBio() != null) {
            tailor.setBio(request.getBio().trim());
        }

        myUserRepository.save(tailor);

        return getTailorProfile(id);
    }

    /**
     * Update specialty
     */
    public boolean updateSpecialty(Long id, String specialty) {
        return myUserRepository.findById(id)
                .filter(user -> "TAILOR".equals(user.getRole()))
                .map(user -> {
                    user.setSpecialty(specialty.trim());
                    myUserRepository.save(user);
                    return true;
                })
                .orElse(false);
    }

    /**
     * Check if user is the owner of the profile
     */
    public boolean isOwner(Long tailorId, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof UserDetails)) {
            return false;
        }

        String username = ((UserDetails) principal).getUsername();
        MyUser currentUser = myUserRepository.findByUsername(username).orElse(null);

        if (currentUser == null) {
            return false;
        }

        return currentUser.getId().equals(tailorId);
    }

    // ========== EXISTING METHODS ==========

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