package stephenowinoh.com.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import stephenowinoh.com.model.Message;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("SELECT m FROM Message m WHERE m.conversation.id = :conversationId ORDER BY m.createdAt DESC")
    List<Message> findByConversationIdOrderByCreatedAtDesc(@Param("conversationId") Long conversationId, Pageable pageable);

    @Query("SELECT m FROM Message m WHERE m.conversation.id = :conversationId ORDER BY m.createdAt DESC")
    List<Message> findByConversationIdOrderByCreatedAtDesc(@Param("conversationId") Long conversationId);

    @Query("SELECT m FROM Message m WHERE m.conversation.id = :conversationId ORDER BY m.createdAt DESC LIMIT 1")
    Message findTopByConversationIdOrderByCreatedAtDesc(@Param("conversationId") Long conversationId);

    @Query("SELECT COUNT(m) FROM Message m " +
            "WHERE m.conversation.id IN (" +
            "    SELECT cp.conversation.id FROM ConversationParticipant cp WHERE cp.user.id = :userId" +
            ") " +
            "AND m.sender.id != :userId " +
            "AND m.isRead = false")
    Long countUnreadMessagesByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(m) FROM Message m " +
            "WHERE m.conversation.id = :conversationId " +
            "AND m.sender.id != :userId " +
            "AND m.isRead = false")
    Long countUnreadInConversation(@Param("conversationId") Long conversationId,
                                   @Param("userId") Long userId);

    @Modifying
    @Query("UPDATE Message m SET m.isRead = true " +
            "WHERE m.conversation.id = :conversationId " +
            "AND m.sender.id != :userId " +
            "AND m.isRead = false")
    void markConversationMessagesAsRead(@Param("conversationId") Long conversationId,
                                        @Param("userId") Long userId);
}