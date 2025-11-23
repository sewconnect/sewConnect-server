package stephenowinoh.com.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stephenowinoh.com.DTO.NotificationDTO;
import stephenowinoh.com.model.MyUser;
import stephenowinoh.com.model.Notification;
import stephenowinoh.com.repository.MyUserRepository;
import stephenowinoh.com.repository.NotificationRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private MyUserRepository userRepository;

    @Transactional
    public void createNotification(Long userId, Notification.NotificationType type,
                                   String message, String referenceId, Long actorId) {
        MyUser user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        MyUser actor = actorId != null ? userRepository.findById(actorId).orElse(null) : null;

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setType(type);
        notification.setMessage(message);
        notification.setReferenceId(referenceId);
        notification.setActor(actor);
        notification.setIsRead(false);

        notificationRepository.save(notification);
    }

    public List<NotificationDTO> getUserNotifications(Long userId) {
        List<Notification> notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return notifications.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<NotificationDTO> getUnreadNotifications(Long userId) {
        List<Notification> notifications = notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId);
        return notifications.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    @Transactional
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setIsRead(true);
        notificationRepository.save(notification);
    }

    @Transactional
    public void markAllAsRead(Long userId) {
        List<Notification> notifications = notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId);
        notifications.forEach(n -> n.setIsRead(true));
        notificationRepository.saveAll(notifications);
    }

    @Transactional
    public void deleteNotification(Long notificationId) {
        notificationRepository.deleteById(notificationId);
    }

    private NotificationDTO convertToDTO(Notification notification) {
        return new NotificationDTO(
                notification.getId(),
                notification.getType().name(),
                notification.getMessage(),
                notification.getReferenceId(),
                notification.getActor() != null ? notification.getActor().getId() : null,
                notification.getActor() != null ? notification.getActor().getFullName() : null,
                notification.getIsRead(),
                notification.getCreatedAt()
        );
    }

    public void notifyNewFollower(Long tailorId, Long followerId) {
        MyUser follower = userRepository.findById(followerId).orElse(null);
        if (follower != null) {
            String message = follower.getFullName() + " started following you";
            createNotification(tailorId, Notification.NotificationType.NEW_FOLLOWER,
                    message, followerId.toString(), followerId);
        }
    }

    public void notifyNewGallery(Long tailorId, Long galleryId, List<Long> followerIds) {
        MyUser tailor = userRepository.findById(tailorId).orElse(null);
        if (tailor != null) {
            String message = tailor.getFullName() + " added a new gallery";
            for (Long followerId : followerIds) {
                createNotification(followerId, Notification.NotificationType.NEW_GALLERY,
                        message, galleryId.toString(), tailorId);
            }
        }
    }
}
