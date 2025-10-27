package stephenowinoh.spring.security.DTO;

import java.util.List;

public class CreateConversationRequest {

    private String type;
    private String groupName;
    private List<Long> participantIds;

    public CreateConversationRequest() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<Long> getParticipantIds() {
        return participantIds;
    }

    public void setParticipantIds(List<Long> participantIds) {
        this.participantIds = participantIds;
    }
}