package stephenowinoh.spring.security.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import stephenowinoh.spring.security.DTO.NotificationDTO;
import stephenowinoh.spring.security.model.MyUser;
import stephenowinoh.spring.security.repository.MyUserRepository;
import stephenowinoh.spring.security.service.NotificationService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "http://localhost:5173")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private MyUserRepository userRepository;

    /**
     * Get all notifications for current user
     */
    @GetMapping
    public ResponseEntity<List<NotificationDTO>> getNotifications(@AuthenticationPrincipal UserDetails userDetails) {
        MyUser user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<NotificationDTO> notifications = notificationService.getUserNotifications(user.getId());
        return ResponseEntity.ok(notifications);
    }

    /**
     * Get unread notifications
     */
    @GetMapping("/unread")
    public ResponseEntity<List<NotificationDTO>> getUnreadNotifications(@AuthenticationPrincipal UserDetails userDetails) {
        MyUser user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<NotificationDTO> notifications = notificationService.getUnreadNotifications(user.getId());
        return ResponseEntity.ok(notifications);
    }

    /**
     * Get unread notification count (for badge)
     */
    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(@AuthenticationPrincipal UserDetails userDetails) {
        MyUser user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Long count = notificationService.getUnreadCount(user.getId());
        Map<String, Long> response = new HashMap<>();
        response.put("count", count);
        return ResponseEntity.ok(response);
    }

    /**
     * Mark notification as read
     */
    @PutMapping("/{id}/read")
    public ResponseEntity<Map<String, String>> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Notification marked as read");
        return ResponseEntity.ok(response);
    }

    /**
     * Mark all notifications as read
     */
    @PutMapping("/read-all")
    public ResponseEntity<Map<String, String>> markAllAsRead(@AuthenticationPrincipal UserDetails userDetails) {
        MyUser user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        notificationService.markAllAsRead(user.getId());
        Map<String, String> response = new HashMap<>();
        response.put("message", "All notifications marked as read");
        return ResponseEntity.ok(response);
    }

    /**
     * Delete a notification
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Notification deleted");
        return ResponseEntity.ok(response);
    }
}