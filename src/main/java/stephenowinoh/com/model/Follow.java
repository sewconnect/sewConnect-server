package stephenowinoh.com.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "follows", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"follower_id", "tailor_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Follow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follower_id", nullable = false)
    private MyUser follower;  // The user who is following

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tailor_id", nullable = false)
    private MyUser tailor;    // The tailor being followed

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "is_active", nullable = false)
    private Boolean active = true;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MyUser getFollower() {
        return follower;
    }

    public void setFollower(MyUser follower) {
        this.follower = follower;
    }

    public MyUser getTailor() {
        return tailor;
    }

    public void setTailor(MyUser tailor) {
        this.tailor = tailor;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    // Convenience methods
    public Boolean isActive() {
        return active;
    }
}