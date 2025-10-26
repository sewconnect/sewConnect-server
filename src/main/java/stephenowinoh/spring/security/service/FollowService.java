package stephenowinoh.spring.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stephenowinoh.spring.security.DTO.FollowDTO;
import stephenowinoh.spring.security.model.Follow;
import stephenowinoh.spring.security.model.MyUser;
import stephenowinoh.spring.security.repository.FollowRepository;
import stephenowinoh.spring.security.repository.MyUserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FollowService {

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private MyUserRepository myUserRepository;

    @Transactional
    public FollowDTO followTailor(Long followerId, Long tailorId) {
        if (followerId.equals(tailorId)) {
            throw new RuntimeException("You cannot follow yourself");
        }

        MyUser follower = myUserRepository.findById(followerId)
                .orElseThrow(() -> new RuntimeException("Follower not found"));

        MyUser tailor = myUserRepository.findById(tailorId)
                .orElseThrow(() -> new RuntimeException("Tailor not found"));

        if (!"TAILOR".equals(tailor.getRole())) {
            throw new RuntimeException("You can only follow tailors");
        }

        if (followRepository.existsByFollowerIdAndTailorIdAndActiveTrue(followerId, tailorId)) {
            throw new RuntimeException("You are already following this tailor");
        }

        Optional<Follow> existingFollow = followRepository.findByFollowerIdAndTailorId(followerId, tailorId);

        Follow follow;
        if (existingFollow.isPresent()) {
            follow = existingFollow.get();
            follow.setActive(true);
        } else {
            follow = new Follow();
            follow.setFollower(follower);
            follow.setTailor(tailor);
            follow.setActive(true);
        }

        Follow saved = followRepository.save(follow);
        return toDTO(saved);
    }

    @Transactional
    public void unfollowTailor(Long followerId, Long tailorId) {
        Follow follow = followRepository
                .findByFollowerIdAndTailorIdAndActiveTrue(followerId, tailorId)
                .orElseThrow(() -> new RuntimeException("Follow relationship not found"));

        follow.setActive(false);
        followRepository.save(follow);
    }

    public boolean isFollowing(Long followerId, Long tailorId) {
        return followRepository.existsByFollowerIdAndTailorIdAndActiveTrue(followerId, tailorId);
    }

    public List<FollowDTO> getFollowers(Long tailorId) {
        return followRepository.findByTailorIdAndActiveTrueOrderByCreatedAtDesc(tailorId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<FollowDTO> getFollowing(Long followerId) {
        return followRepository.findByFollowerIdAndActiveTrueOrderByCreatedAtDesc(followerId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public long getFollowerCount(Long tailorId) {
        return followRepository.countByTailorIdAndActiveTrue(tailorId);
    }

    public long getFollowingCount(Long followerId) {
        return followRepository.countByFollowerIdAndActiveTrue(followerId);
    }

    private FollowDTO toDTO(Follow follow) {
        return new FollowDTO(
                follow.getId(),
                follow.getFollower().getId(),
                follow.getFollower().getUsername(),
                follow.getFollower().getEmail(),
                follow.getTailor().getId(),
                follow.getTailor().getUsername(),
                follow.getTailor().getEmail(),
                follow.getCreatedAt()
        );
    }
}