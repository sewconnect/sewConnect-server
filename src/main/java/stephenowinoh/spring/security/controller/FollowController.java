package stephenowinoh.spring.security.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import stephenowinoh.spring.security.DTO.FollowDTO;
import stephenowinoh.spring.security.model.MyUser;
import stephenowinoh.spring.security.repository.MyUserRepository;
import stephenowinoh.spring.security.service.FollowService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/follows")
@CrossOrigin(origins = "*")
public class FollowController {

    @Autowired
    private FollowService followService;

    @Autowired
    private MyUserRepository myUserRepository;

    /**
     * POST /api/follows/{tailorId}
     * Follow a tailor - Gets user from JWT token
     */
    @PostMapping("/{tailorId}")
    public ResponseEntity<?> followTailor(
            @PathVariable Long tailorId,
            Authentication authentication) {
        try {
            // Check authentication
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity
                        .status(401)
                        .body(Map.of("message", "Please login to follow a tailor"));
            }

            // Get username from authenticated user
            String username = authentication.getName();
            System.out.println("✅ Authenticated user: " + username);

            // Find user in database
            MyUser follower = myUserRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            System.out.println("✅ Follower ID: " + follower.getId());
            System.out.println("✅ Tailor ID: " + tailorId);

            // Perform follow operation
            FollowDTO follow = followService.followTailor(follower.getId(), tailorId);

            System.out.println("✅ Follow successful!");
            return ResponseEntity.ok(follow);

        } catch (RuntimeException e) {
            System.err.println("❌ Error: " + e.getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * DELETE /api/follows/{tailorId}
     * Unfollow a tailor - Gets user from JWT token
     */
    @DeleteMapping("/{tailorId}")
    public ResponseEntity<?> unfollowTailor(
            @PathVariable Long tailorId,
            Authentication authentication) {
        try {
            // Check authentication
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity
                        .status(401)
                        .body(Map.of("message", "Please login to unfollow a tailor"));
            }

            // Get username from authenticated user
            String username = authentication.getName();

            // Find user in database
            MyUser follower = myUserRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Perform unfollow operation
            followService.unfollowTailor(follower.getId(), tailorId);

            return ResponseEntity.ok(Map.of("message", "Unfollowed successfully"));

        } catch (RuntimeException e) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * GET /api/follows/is-following/{tailorId}
     * Check if current user is following a tailor - Gets user from JWT token
     */
    @GetMapping("/is-following/{tailorId}")
    public ResponseEntity<?> isFollowing(
            @PathVariable Long tailorId,
            Authentication authentication) {
        try {
            // If not authenticated, return false
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.ok(Map.of("isFollowing", false));
            }

            // Get username from authenticated user
            String username = authentication.getName();

            // Find user in database
            MyUser follower = myUserRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Check follow status
            boolean isFollowing = followService.isFollowing(follower.getId(), tailorId);

            return ResponseEntity.ok(Map.of("isFollowing", isFollowing));

        } catch (RuntimeException e) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * GET /api/follows/tailor/{tailorId}/followers
     * Get all followers of a tailor
     */
    @GetMapping("/tailor/{tailorId}/followers")
    public ResponseEntity<List<FollowDTO>> getFollowers(@PathVariable Long tailorId) {
        List<FollowDTO> followers = followService.getFollowers(tailorId);
        return ResponseEntity.ok(followers);
    }

    /**
     * GET /api/follows/user/{userId}/following
     * Get all tailors a user is following
     */
    @GetMapping("/user/{userId}/following")
    public ResponseEntity<List<FollowDTO>> getFollowing(@PathVariable Long userId) {
        List<FollowDTO> following = followService.getFollowing(userId);
        return ResponseEntity.ok(following);
    }

    /**
     * GET /api/follows/tailor/{tailorId}/count
     * Get follower count for a tailor
     */
    @GetMapping("/tailor/{tailorId}/count")
    public ResponseEntity<Map<String, Long>> getFollowerCount(@PathVariable Long tailorId) {
        long count = followService.getFollowerCount(tailorId);
        return ResponseEntity.ok(Map.of("followerCount", count));
    }

    /**
     * GET /api/follows/user/{userId}/following-count
     * Get count of tailors a user is following
     */
    @GetMapping("/user/{userId}/following-count")
    public ResponseEntity<Map<String, Long>> getFollowingCount(@PathVariable Long userId) {
        long count = followService.getFollowingCount(userId);
        return ResponseEntity.ok(Map.of("followingCount", count));
    }
}