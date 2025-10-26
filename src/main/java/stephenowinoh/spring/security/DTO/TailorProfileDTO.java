package stephenowinoh.spring.security.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * Enhanced DTO for Tailor Profile Page
 * Includes tailor info + services + galleries + followers
 */

@NoArgsConstructor
@AllArgsConstructor
public class TailorProfileDTO {

    // Basic Tailor Info
    private Long id;
    private String fullName;
    private String username;
    private String phoneNumber;
    private String specialty;
    private String location;
    private String nationality;

    // Profile Stats
    private Integer totalGalleries;
    private Integer totalFollowers;
    private Integer totalFollowing;

    // About section
    private String bio;

    // Services & Pricing
    private List<ServiceDTO> services;

    // Galleries
    private List<GalleryDTO> galleries;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public Integer getTotalGalleries() {
        return totalGalleries;
    }

    public void setTotalGalleries(Integer totalGalleries) {
        this.totalGalleries = totalGalleries;
    }

    public Integer getTotalFollowers() {
        return totalFollowers;
    }

    public void setTotalFollowers(Integer totalFollowers) {
        this.totalFollowers = totalFollowers;
    }

    public Integer getTotalFollowing() {
        return totalFollowing;
    }

    public void setTotalFollowing(Integer totalFollowing) {
        this.totalFollowing = totalFollowing;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public List<ServiceDTO> getServices() {
        return services;
    }

    public void setServices(List<ServiceDTO> services) {
        this.services = services;
    }

    public List<GalleryDTO> getGalleries() {
        return galleries;
    }

    public void setGalleries(List<GalleryDTO> galleries) {
        this.galleries = galleries;
    }
}
