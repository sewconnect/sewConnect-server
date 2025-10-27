package stephenowinoh.spring.security.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import stephenowinoh.spring.security.DTO.*;
import stephenowinoh.spring.security.model.MyUser;
import stephenowinoh.spring.security.service.ChatService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
public class ChatController {

    @Autowired
    private ChatService chatService;

    private Long getCurrentUserId(Authentication authentication) {
        MyUser user = (MyUser) authentication.getPrincipal();
        return user.getId();
    }

    @PostMapping("/conversations")
    public ResponseEntity<ConversationDTO> createConversation(
            @RequestBody CreateConversationRequest request,
            Authentication authentication) {

        Long userId = getCurrentUserId(authentication);
        ConversationDTO conversation = chatService.createConversation(request, userId);
        return ResponseEntity.ok(conversation);
    }

    @GetMapping("/conversations/direct/{otherUserId}")
    public ResponseEntity<ConversationDTO> getOrCreateDirectConversation(
            @PathVariable Long otherUserId,
            Authentication authentication) {

        Long userId = getCurrentUserId(authentication);
        ConversationDTO conversation = chatService.getOrCreateDirectConversation(userId, otherUserId);
        return ResponseEntity.ok(conversation);
    }

    @GetMapping("/conversations")
    public ResponseEntity<List<ConversationDTO>> getUserConversations(Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        List<ConversationDTO> conversations = chatService.getUserConversations(userId);
        return ResponseEntity.ok(conversations);
    }

    @GetMapping("/conversations/{conversationId}")
    public ResponseEntity<ConversationDTO> getConversation(
            @PathVariable Long conversationId,
            Authentication authentication) {

        Long userId = getCurrentUserId(authentication);
        ConversationDTO conversation = chatService.getConversationById(conversationId, userId);
        return ResponseEntity.ok(conversation);
    }

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

    @PostMapping("/conversations/{conversationId}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable Long conversationId,
            Authentication authentication) {

        Long userId = getCurrentUserId(authentication);
        chatService.markAsRead(conversationId, userId);
        return ResponseEntity.ok().build();
    }

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
}