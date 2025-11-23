package stephenowinoh.com.DTO;

import java.time.LocalDateTime;

public class NotificationDTO {
    private Long id;
    private String type;
    private String message;
    private String referenceId;
    private Long actorId;
    private String actorName;
    private Boolean isRead;
    private LocalDateTime createdAt;

    public NotificationDTO() {}

    public NotificationDTO(Long id, String type, String message, String referenceId,
                           Long actorId, String actorName, Boolean isRead, LocalDateTime createdAt) {
        this.id = id;
        this.type = type;
        this.message = message;
        this.referenceId = referenceId;
        this.actorId = actorId;
        this.actorName = actorName;
        this.isRead = isRead;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public Long getActorId() {
        return actorId;
    }

    public void setActorId(Long actorId) {
        this.actorId = actorId;
    }

    public String getActorName() {
        return actorName;
    }

    public void setActorName(String actorName) {
        this.actorName = actorName;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}