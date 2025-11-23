package stephenowinoh.com.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import stephenowinoh.com.DTO.SendMessageRequest;
import stephenowinoh.com.model.ConversationParticipant;
import stephenowinoh.com.model.MyUser;
import stephenowinoh.com.repository.ConversationParticipantRepository;
import stephenowinoh.com.repository.ConversationRepository;
import stephenowinoh.com.service.ChatService;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ChatWebSocketController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private ConversationParticipantRepository participantRepository;

    @MessageMapping("/chat.send")
    public void sendMessage(@Payload SendMessageRequest request, Principal principal) {
        MyUser user = (MyUser) principal;
        Long senderId = user.getId();

        chatService.sendMessage(request, senderId);
    }

    @MessageMapping("/chat.typing")
    public void handleTyping(@Payload TypingNotification notification, Principal principal) {
        try {
            MyUser user = (MyUser) principal;
            Long userId = user.getId();
            String username = user.getUsername();

            System.out.println("=== TYPING STATUS RECEIVED ===");
            System.out.println("User ID: " + userId);
            System.out.println("Username: " + username);
            System.out.println("Conversation ID: " + notification.getConversationId());
            System.out.println("Is Typing: " + notification.isTyping());

            // Get conversation participants
            List<ConversationParticipant> participants = participantRepository
                    .findByConversationId(notification.getConversationId());

            if (participants.isEmpty()) {
                System.err.println("❌ No participants found for conversation: " + notification.getConversationId());
                return;
            }

            // Create typing status payload
            Map<String, Object> typingStatus = new HashMap<>();
            typingStatus.put("conversationId", notification.getConversationId());
            typingStatus.put("userId", userId);
            typingStatus.put("username", username);
            typingStatus.put("isTyping", notification.isTyping());

            // Send typing status to all participants except sender
            for (ConversationParticipant participant : participants) {
                if (!participant.getUser().getId().equals(userId)) {
                    String destination = "/queue/typing";
                    messagingTemplate.convertAndSendToUser(
                            participant.getUser().getId().toString(),
                            destination,
                            typingStatus
                    );
                    System.out.println("✅ Typing status sent to user: " + participant.getUser().getId());
                }
            }

        } catch (Exception e) {
            System.err.println("❌ Error handling typing status: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static class TypingNotification {
        private Long conversationId;
        private boolean isTyping;

        public TypingNotification() {
        }

        public TypingNotification(Long conversationId, boolean isTyping) {
            this.conversationId = conversationId;
            this.isTyping = isTyping;
        }

        public Long getConversationId() {
            return conversationId;
        }

        public void setConversationId(Long conversationId) {
            this.conversationId = conversationId;
        }

        public boolean isTyping() {
            return isTyping;
        }

        public void setTyping(boolean typing) {
            isTyping = typing;
        }
    }
}