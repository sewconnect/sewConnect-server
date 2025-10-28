package stephenowinoh.spring.security.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import stephenowinoh.spring.security.DTO.*;
import stephenowinoh.spring.security.model.MyUser;
import stephenowinoh.spring.security.service.ChatService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private Long getCurrentUserId(Authentication authentication) {
        MyUser user = (MyUser) authentication.getPrincipal();
        return user.getId();
    }

    // ✅ CREATE OR GET DIRECT CONVERSATION
    @GetMapping("/conversations/direct/{otherUserId}")
    public ResponseEntity<ConversationDTO> getOrCreateDirectConversation(
            @PathVariable Long otherUserId,
            Authentication authentication) {

        Long currentUserId = getCurrentUserId(authentication);
        System.out.println("=== GET/CREATE DIRECT CONVERSATION ===");
        System.out.println("Current User ID: " + currentUserId);
        System.out.println("Other User ID: " + otherUserId);

        ConversationDTO conversation = chatService.getOrCreateDirectConversation(currentUserId, otherUserId);
        System.out.println("✅ Conversation created/retrieved: " + conversation.getId());

        return ResponseEntity.ok(conversation);
    }

    // GET ALL CONVERSATIONS FOR CURRENT USER
    @GetMapping("/conversations")
    public ResponseEntity<List<ConversationDTO>> getUserConversations(Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        List<ConversationDTO> conversations = chatService.getUserConversations(userId);
        return ResponseEntity.ok(conversations);
    }

    // GET CONVERSATION BY ID
    @GetMapping("/conversations/{conversationId}")
    public ResponseEntity<ConversationDTO> getConversationById(
            @PathVariable Long conversationId,
            Authentication authentication) {

        Long userId = getCurrentUserId(authentication);
        ConversationDTO conversation = chatService.getConversationById(conversationId, userId);
        return ResponseEntity.ok(conversation);
    }

    // GET MESSAGES FOR A CONVERSATION
    @GetMapping("/conversations/{conversationId}/messages")
    public ResponseEntity<List<ChatMessageDTO>> getMessages(
            @PathVariable Long conversationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            Authentication authentication) {

        Long userId = getCurrentUserId(authentication);
        List<ChatMessageDTO> messages = chatService.getConversationMessages(conversationId, userId, page, size);
        return ResponseEntity.ok(messages);
    }

    // SEND A MESSAGE
    @PostMapping("/conversations/{conversationId}/messages")
    public ResponseEntity<ChatMessageDTO> sendMessage(
            @PathVariable Long conversationId,
            @RequestBody Map<String, String> payload,
            Authentication authentication) {

        Long userId = getCurrentUserId(authentication);
        SendMessageRequest request = new SendMessageRequest(conversationId, payload.get("content"));
        ChatMessageDTO message = chatService.sendMessage(request, userId);
        return ResponseEntity.ok(message);
    }

    // MARK CONVERSATION AS READ
    @PostMapping("/conversations/{conversationId}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable Long conversationId,
            Authentication authentication) {

        Long userId = getCurrentUserId(authentication);
        chatService.markAsRead(conversationId, userId);
        return ResponseEntity.ok().build();
    }

    // ✅ NEW: GET UNREAD MESSAGE COUNT
    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadMessageCount(Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        Long unreadCount = chatService.getUnreadMessageCount(userId);

        Map<String, Long> response = new HashMap<>();
        response.put("count", unreadCount);

        return ResponseEntity.ok(response);
    }

    // CREATE GROUP CONVERSATION
    @PostMapping("/conversations")
    public ResponseEntity<ConversationDTO> createGroupConversation(
            @RequestBody CreateConversationRequest request,
            Authentication authentication) {

        Long userId = getCurrentUserId(authentication);
        ConversationDTO conversation = chatService.createConversation(request, userId);
        return ResponseEntity.ok(conversation);
    }

    // ADD PARTICIPANTS TO CONVERSATION
    @PostMapping("/conversations/{conversationId}/participants")
    public ResponseEntity<ConversationDTO> addParticipants(
            @PathVariable Long conversationId,
            @RequestBody Map<String, List<Long>> payload,
            Authentication authentication) {

        Long userId = getCurrentUserId(authentication);
        List<Long> userIds = payload.get("userIds");
        ConversationDTO conversation = chatService.addParticipants(conversationId, userIds, userId);
        return ResponseEntity.ok(conversation);
    }

    @GetMapping("/conversations/debug")
    public ResponseEntity<Map<String, Object>> debugConversations(Authentication authentication) {
        Long userId = getCurrentUserId(authentication);

        Map<String, Object> debug = new HashMap<>();
        debug.put("currentUserId", userId);
        debug.put("conversationsCount", chatService.getUserConversations(userId).size());
        debug.put("conversations", chatService.getUserConversations(userId));

        return ResponseEntity.ok(debug);
    }
}