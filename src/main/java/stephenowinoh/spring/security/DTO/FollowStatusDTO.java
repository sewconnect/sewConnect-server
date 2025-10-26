package stephenowinoh.spring.security.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
class FollowStatusDTO {
    private boolean isFollowing;
    private Long followId;

    public boolean isFollowing() {
        return isFollowing;
    }

    public void setFollowing(boolean following) {
        isFollowing = following;
    }

    public Long getFollowId() {
        return followId;
    }

    public void setFollowId(Long followId) {
        this.followId = followId;
    }
}
