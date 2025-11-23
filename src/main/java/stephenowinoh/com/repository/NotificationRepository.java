package stephenowinoh.com.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stephenowinoh.com.model.Notification;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // Get all notifications for a user
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);

    // Get unread notifications for a user
    List<Notification> findByUserIdAndIsReadFalseOrderByCreatedAtDesc(Long userId);

    // Count unread notifications
    Long countByUserIdAndIsReadFalse(Long userId);

    // Get recent unread notifications (for header badge)
    List<Notification> findTop10ByUserIdAndIsReadFalseOrderByCreatedAtDesc(Long userId);
}