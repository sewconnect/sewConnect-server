package stephenowinoh.com.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor

public class FollowDTO {
    private Long id;
    private Long followerId;
    private String followerName;
    private String followerEmail;
    private Long tailorId;
    private String tailorName;
    private String tailorEmail;
    private LocalDateTime createdAt;

    public FollowDTO(Long id, Long followerId, String followerName, String followerEmail, Long tailorId, String tailorName, String tailorEmail, LocalDateTime createdAt) {
        this.id = id;
        this.followerId = followerId;
        this.followerName = followerName;
        this.followerEmail = followerEmail;
        this.tailorId = tailorId;
        this.tailorName = tailorName;
        this.tailorEmail = tailorEmail;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFollowerId() {
        return followerId;
    }

    public void setFollowerId(Long followerId) {
        this.followerId = followerId;
    }

    public String getFollowerName() {
        return followerName;
    }

    public void setFollowerName(String followerName) {
        this.followerName = followerName;
    }

    public String getFollowerEmail() {
        return followerEmail;
    }

    public void setFollowerEmail(String followerEmail) {
        this.followerEmail = followerEmail;
    }

    public Long getTailorId() {
        return tailorId;
    }

    public void setTailorId(Long tailorId) {
        this.tailorId = tailorId;
    }

    public String getTailorName() {
        return tailorName;
    }

    public void setTailorName(String tailorName) {
        this.tailorName = tailorName;
    }

    public String getTailorEmail() {
        return tailorEmail;
    }

    public void setTailorEmail(String tailorEmail) {
        this.tailorEmail = tailorEmail;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}



