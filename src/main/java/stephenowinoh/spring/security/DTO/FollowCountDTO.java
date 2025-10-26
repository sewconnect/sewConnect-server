package stephenowinoh.spring.security.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
class FollowCountDTO {
    private long followerCount;
    private long followingCount;

    public long getFollowerCount() {
        return followerCount;
    }

    public void setFollowerCount(long followerCount) {
        this.followerCount = followerCount;
    }

    public long getFollowingCount() {
        return followingCount;
    }

    public void setFollowingCount(long followingCount) {
        this.followingCount = followingCount;
    }
}
