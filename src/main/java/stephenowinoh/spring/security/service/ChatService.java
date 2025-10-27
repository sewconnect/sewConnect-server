package stephenowinoh.spring.security.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stephenowinoh.spring.security.DTO.*;
import stephenowinoh.spring.security.config.RabbitMQConfig;
import stephenowinoh.spring.security.model.*;
import stephenowinoh.spring.security.repository.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ChatService {

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private ConversationParticipantRepository participantRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private MyUserRepository userRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Transactional
    public ConversationDTO createConversation(CreateConversationRequest request, Long creatorId) {
        MyUser creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if ("GROUP".equals(request.getType()) && !"TAILOR".equals(creator.getRole())) {
            throw new RuntimeException("Only tailors can create group chats");
        }

        Conversation conversation = new Conversation();
        conversation.setType(Conversation.ConversationType.valueOf(request.getType()));
        conversation.setCreatedBy(creator);

        if ("GROUP".equals(request.getType())) {
            conversation.setGroupName(request.getGroupName());
        }

        conversation = conversationRepository.save(conversation);

        ConversationParticipant creatorParticipant = new ConversationParticipant();
        creatorParticipant.setConversation(conversation);
        creatorParticipant.setUser(creator);
        participantRepository.save(creatorParticipant);

        for (Long participantId : request.getParticipantIds()) {
            if (!participantId.equals(creatorId)) {
                MyUser user = userRepository.findById(participantId)
                        .orElseThrow(() -> new RuntimeException("User not found: " + participantId));

                ConversationParticipant participant = new ConversationParticipant();
                participant.setConversation(conversation);
                participant.setUser(user);
                participantRepository.save(participant);
            }
        }

        return convertToDTO(conversation, creatorId);
    }

    @Transactional
    public ConversationDTO getOrCreateDirectConversation(Long user1Id, Long user2Id) {
        Optional<Conversation> existingConversation = conversationRepository
                .findDirectConversation(user1Id, user2Id);

        if (existingConversation.isPresent()) {
            return convertToDTO(existingConversation.get(), user1Id);
        }

        CreateConversationRequest request = new CreateConversationRequest();
        request.setType("DIRECT");
        request.setParticipantIds(List.of(user2Id));

        return createConversation(request, user1Id);
    }

    @Transactional(readOnly = true)
    public List<ConversationDTO> getUserConversations(Long userId) {
        List<Conversation> conversations = conversationRepository.findByUserId(userId);
        return conversations.stream()
                .map(conv -> convertToDTO(conv, userId))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ConversationDTO getConversationById(Long conversationId, Long userId) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found"));

        boolean isParticipant = participantRepository
                .existsByConversationIdAndUserId(conversationId, userId);

        if (!isParticipant) {
            throw new RuntimeException("User is not a participant in this conversation");
        }

        return convertToDTO(conversation, userId);
    }

    @Transactional
    public ChatMessageDTO sendMessage(SendMessageRequest request, Long senderId) {
        Conversation conversation = conversationRepository.findById(request.getConversationId())
                .orElseThrow(() -> new RuntimeException("Conversation not found"));

        boolean isParticipant = participantRepository
                .existsByConversationIdAndUserId(request.getConversationId(), senderId);

        if (!isParticipant) {
            throw new RuntimeException("User is not a participant in this conversation");
        }

        MyUser sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Message message = new Message();
        message.setConversation(conversation);
        message.setSender(sender);
        message.setContent(request.getContent());
        message.setIsEdited(false);

        message = messageRepository.save(message);

        ChatMessageDTO messageDTO = convertMessageToDTO(message);

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.CHAT_EXCHANGE,
                RabbitMQConfig.CHAT_ROUTING_KEY,
                messageDTO
        );

        List<ConversationParticipant> participants = participantRepository
                .findByConversationId(conversation.getId());

        for (ConversationParticipant participant : participants) {
            messagingTemplate.convertAndSendToUser(
                    participant.getUser().getUsername(),
                    "/queue/messages",
                    messageDTO
            );

        }

        return messageDTO;
    }

    @Transactional(readOnly = true)
    public List<ChatMessageDTO> getConversationMessages(Long conversationId, Long userId, int page, int size) {
        boolean isParticipant = participantRepository
                .existsByConversationIdAndUserId(conversationId, userId);

        if (!isParticipant) {
            throw new RuntimeException("User is not a participant in this conversation");
        }

        List<Message> messages = messageRepository.findByConversationIdOrderByCreatedAtDesc(
                conversationId,
                PageRequest.of(page, size)
        );

        List<ChatMessageDTO> messageDTOs = messages.stream()
                .map(this::convertMessageToDTO)
                .collect(Collectors.toList());

        java.util.Collections.reverse(messageDTOs);
        return messageDTOs;
    }

    @Transactional
    public void markAsRead(Long conversationId, Long userId) {
        ConversationParticipant participant = participantRepository
                .findByConversationIdAndUserId(conversationId, userId)
                .orElseThrow(() -> new RuntimeException("Participant not found"));

        participant.setLastReadAt(LocalDateTime.now());
        participantRepository.save(participant);
    }

    @Transactional
    public ConversationDTO addParticipants(Long conversationId, List<Long> userIds, Long requesterId) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found"));

        if (conversation.getType() != Conversation.ConversationType.GROUP) {
            throw new RuntimeException("Can only add participants to group conversations");
        }

        if (!conversation.getCreatedBy().getId().equals(requesterId)) {
            throw new RuntimeException("Only the creator can add participants");
        }

        for (Long userId : userIds) {
            if (!participantRepository.existsByConversationIdAndUserId(conversationId, userId)) {
                MyUser user = userRepository.findById(userId)
                        .orElseThrow(() -> new RuntimeException("User not found: " + userId));

                ConversationParticipant participant = new ConversationParticipant();
                participant.setConversation(conversation);
                participant.setUser(user);
                participantRepository.save(participant);
            }
        }

        return convertToDTO(conversation, requesterId);
    }

    private ConversationDTO convertToDTO(Conversation conversation, Long currentUserId) {
        ConversationDTO dto = new ConversationDTO();
        dto.setId(conversation.getId());
        dto.setType(conversation.getType().name());
        dto.setGroupName(conversation.getGroupName());
        dto.setCreatedById(conversation.getCreatedBy().getId());
        dto.setCreatedByName(conversation.getCreatedBy().getFullName());
        dto.setCreatedAt(conversation.getCreatedAt());

        List<ConversationParticipant> participants = participantRepository
                .findByConversationId(conversation.getId());

        List<ParticipantDTO> participantDTOs = participants.stream()
                .map(p -> new ParticipantDTO(
                        p.getUser().getId(),
                        p.getUser().getFullName(),
                        p.getUser().getRole(),
                        p.getJoinedAt()
                ))
                .collect(Collectors.toList());
        dto.setParticipants(participantDTOs);

        List<Message> lastMessages = messageRepository.findByConversationIdOrderByCreatedAtDesc(
                conversation.getId(),
                PageRequest.of(0, 1)
        );

        if (!lastMessages.isEmpty()) {
            dto.setLastMessage(convertMessageToDTO(lastMessages.get(0)));
        }

        ConversationParticipant currentParticipant = participantRepository
                .findByConversationIdAndUserId(conversation.getId(), currentUserId)
                .orElse(null);

        if (currentParticipant != null && currentParticipant.getLastReadAt() != null) {
            List<Message> allMessages = messageRepository
                    .findByConversationIdOrderByCreatedAtDesc(conversation.getId());

            long unreadCount = allMessages.stream()
                    .filter(m -> m.getCreatedAt().isAfter(currentParticipant.getLastReadAt())
                            && !m.getSender().getId().equals(currentUserId))
                    .count();
            dto.setUnreadCount((int) unreadCount);
        } else {
            dto.setUnreadCount(0);
        }

        return dto;
    }

    private ChatMessageDTO convertMessageToDTO(Message message) {
        return new ChatMessageDTO(
                message.getId(),
                message.getConversation().getId(),
                message.getSender().getId(),
                message.getSender().getFullName(),
                message.getContent(),
                message.getCreatedAt(),
                message.getIsEdited()
        );
    }
}