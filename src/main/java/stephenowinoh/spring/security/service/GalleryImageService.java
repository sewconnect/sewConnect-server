package stephenowinoh.spring.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stephenowinoh.spring.security.DTO.GalleryImageDTO;
import stephenowinoh.spring.security.model.Gallery;
import stephenowinoh.spring.security.model.GalleryImage;
import stephenowinoh.spring.security.repository.GalleryImageRepository;
import stephenowinoh.spring.security.repository.GalleryRepository;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GalleryImageService {

    @Autowired
    private GalleryImageRepository galleryImageRepository;

    @Autowired
    private GalleryRepository galleryRepository;

    private static final int MAX_IMAGES_PER_GALLERY = 300;

    public List<GalleryImageDTO> getImagesByGalleryId(Long galleryId) {
        List<GalleryImage> images = galleryImageRepository.findByGalleryIdOrderByUploadedAtDesc(galleryId);
        return images.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Transactional
    public GalleryImageDTO addImageToGallery(Long galleryId, GalleryImageDTO imageDTO) {
        Gallery gallery = galleryRepository.findById(galleryId)
                .orElseThrow(() -> new RuntimeException("Gallery not found"));

        long currentCount = galleryImageRepository.countByGalleryId(galleryId);
        if (currentCount >= MAX_IMAGES_PER_GALLERY) {
            throw new RuntimeException("Gallery has reached maximum capacity of 300 images");
        }

        GalleryImage image = new GalleryImage();
        image.setGallery(gallery);
        image.setImageUrl(imageDTO.getImageUrl());  // Use directly - already verified!
        image.setPublicId(imageDTO.getPublicId());
        image.setCaption(imageDTO.getCaption());

        GalleryImage saved = galleryImageRepository.save(image);

        gallery.setImageCount(gallery.getImageCount() + 1);
        galleryRepository.save(gallery);

        return convertToDTO(saved);
    }

    @Transactional
    public void deleteImage(Long imageId) {
        GalleryImage image = galleryImageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found"));

        Gallery gallery = image.getGallery();
        gallery.setImageCount(gallery.getImageCount() - 1);
        galleryRepository.save(gallery);

        galleryImageRepository.delete(image);
    }

    public boolean canAddImage(Long galleryId) {
        long currentCount = galleryImageRepository.countByGalleryId(galleryId);
        return currentCount < MAX_IMAGES_PER_GALLERY;
    }

    /**
     * Process Cloudinary image URL to ensure proper format
     * Removes file extensions and adds format transformation for consistent display
     */
    private String processImageUrl(String originalUrl) {
        if (originalUrl == null || !originalUrl.contains("cloudinary.com")) {
            return originalUrl;
        }

        // Remove file extension (avif, webp, jpg, jpeg, png)
        String processedUrl = originalUrl.replaceAll("\\.(avif|webp|jpg|jpeg|png)$", "");

        // Add format transformation to force JPG (better browser compatibility)
        if (processedUrl.contains("/upload/")) {
            processedUrl = processedUrl.replace("/upload/", "/upload/f_jpg,q_auto/");
        }

        System.out.println("=== IMAGE URL PROCESSING ===");
        System.out.println("Original URL: " + originalUrl);
        System.out.println("Processed URL: " + processedUrl);
        System.out.println("===========================");

        return processedUrl;
    }

    private GalleryImageDTO convertToDTO(GalleryImage image) {
        GalleryImageDTO dto = new GalleryImageDTO();
        dto.setId(image.getId());
        dto.setGalleryId(image.getGallery().getId());
        dto.setImageUrl(image.getImageUrl());
        dto.setPublicId(image.getPublicId());
        dto.setCaption(image.getCaption());
        dto.setUploadedAt(image.getUploadedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        return dto;
    }
}