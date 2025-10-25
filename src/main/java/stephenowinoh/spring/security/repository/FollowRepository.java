package stephenowinoh.spring.security.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stephenowinoh.spring.security.model.Follow;
import stephenowinoh.spring.security.model.MyUser;

import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {

    // Count followers (people who follow this user)
    long countByFollowing(MyUser following);

    // Count following (people this user follows)
    long countByFollower(MyUser follower);

    // Check if user A follows user B
    boolean existsByFollowerAndFollowing(MyUser follower, MyUser following);

    // Find specific follow relationship
    Optional<Follow> findByFollowerAndFollowing(MyUser follower, MyUser following);

    // Delete follow relationship
    void deleteByFollowerAndFollowing(MyUser follower, MyUser following);
}
