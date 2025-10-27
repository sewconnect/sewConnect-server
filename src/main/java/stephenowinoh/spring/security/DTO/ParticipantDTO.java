package stephenowinoh.spring.security.DTO;

import java.time.LocalDateTime;

public class ParticipantDTO {

    private Long userId;
    private String fullName;
    private String role;
    private LocalDateTime joinedAt;

    public ParticipantDTO() {
    }

    public ParticipantDTO(Long userId, String fullName, String role, LocalDateTime joinedAt) {
        this.userId = userId;
        this.fullName = fullName;
        this.role = role;
        this.joinedAt = joinedAt;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(LocalDateTime joinedAt) {
        this.joinedAt = joinedAt;
    }
}