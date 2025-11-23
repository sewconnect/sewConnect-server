package stephenowinoh.com.DTO;

public class SendMessageRequest {

    private Long conversationId;
    private String content;

    public SendMessageRequest() {
    }

    public SendMessageRequest(Long conversationId, String content) {
        this.conversationId = conversationId;
        this.content = content;
    }

    public Long getConversationId() {
        return conversationId;
    }

    public void setConversationId(Long conversationId) {
        this.conversationId = conversationId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}