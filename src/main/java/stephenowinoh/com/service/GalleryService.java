package stephenowinoh.com.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stephenowinoh.com.DTO.GalleryDTO;
import stephenowinoh.com.model.Follow;
import stephenowinoh.com.model.Gallery;
import stephenowinoh.com.model.MyUser;
import stephenowinoh.com.repository.FollowRepository;
import stephenowinoh.com.repository.GalleryRepository;
import stephenowinoh.com.repository.MyUserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GalleryService {

    @Autowired
    private GalleryRepository galleryRepository;

    @Autowired
    private MyUserRepository myUserRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private FollowRepository followRepository;

    private static final int MAX_GALLERIES_PER_TAILOR = 12;

    public List<GalleryDTO> getGalleriesByTailorId(Long tailorId) {
        return galleryRepository.findByTailorIdAndIsActiveTrueOrderByCreatedAtDesc(tailorId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public GalleryDTO getGalleryById(Long galleryId) {
        Gallery gallery = galleryRepository.findById(galleryId)
                .orElseThrow(() -> new RuntimeException("Gallery not found"));

        if (!gallery.getIsActive()) {
            throw new RuntimeException("Gallery has been deleted");
        }

        return toDTO(gallery);
    }

    @Transactional
    public GalleryDTO createGallery(Long tailorId, GalleryDTO galleryDTO) {
        MyUser tailor = myUserRepository.findById(tailorId)
                .orElseThrow(() -> new RuntimeException("Tailor not found"));

        if (!"TAILOR".equals(tailor.getRole())) {
            throw new RuntimeException("User is not a tailor");
        }

        long galleryCount = galleryRepository.countByTailorIdAndIsActiveTrue(tailorId);
        if (galleryCount >= MAX_GALLERIES_PER_TAILOR) {
            throw new RuntimeException("Maximum gallery limit reached (12 galleries)");
        }

        Gallery gallery = new Gallery();
        gallery.setTailor(tailor);
        gallery.setGalleryName(galleryDTO.getGalleryName());
        gallery.setDescription(galleryDTO.getDescription());
        gallery.setThumbnailUrl(galleryDTO.getThumbnailUrl());
        gallery.setImageCount(0);
        gallery.setIsActive(true);

        Gallery saved = galleryRepository.save(gallery);

        List<Follow> followers = followRepository.findByTailorIdAndActiveTrueOrderByCreatedAtDesc(tailorId);
        List<Long> followerIds = followers.stream()
                .map(follow -> follow.getFollower().getId())
                .collect(Collectors.toList());

        if (!followerIds.isEmpty()) {
            notificationService.notifyNewGallery(tailorId, saved.getId(), followerIds);
        }

        return toDTO(saved);
    }

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

    @Transactional
    public void deleteGallery(Long galleryId) {
        Gallery gallery = galleryRepository.findById(galleryId)
                .orElseThrow(() -> new RuntimeException("Gallery not found"));

        gallery.setIsActive(false);
        galleryRepository.save(gallery);
    }

    public long getGalleryCount(Long tailorId) {
        return galleryRepository.countByTailorIdAndIsActiveTrue(tailorId);
    }

    public boolean canCreateGallery(Long tailorId) {
        return galleryRepository.countByTailorIdAndIsActiveTrue(tailorId) < MAX_GALLERIES_PER_TAILOR;
    }

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