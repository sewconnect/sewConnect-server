package stephenowinoh.com.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import stephenowinoh.com.model.Follow;
import stephenowinoh.com.model.MyUser;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {

    boolean existsByFollowerIdAndTailorIdAndActiveTrue(Long followerId, Long tailorId);

    Optional<Follow> findByFollowerIdAndTailorIdAndActiveTrue(Long followerId, Long tailorId);

    Optional<Follow> findByFollowerIdAndTailorId(Long followerId, Long tailorId);

    List<Follow> findByTailorIdAndActiveTrueOrderByCreatedAtDesc(Long tailorId);

    List<Follow> findByFollowerIdAndActiveTrueOrderByCreatedAtDesc(Long followerId);

    long countByTailorIdAndActiveTrue(Long tailorId);

    long countByFollowerIdAndActiveTrue(Long followerId);

    Long countByFollower(MyUser follower);

    Long countByTailor(MyUser tailor);

    @Query("SELECT f FROM Follow f WHERE f.tailor.id = :tailorId AND f.active = true AND f.tailor.role = 'TAILOR' ORDER BY f.createdAt DESC")
    List<Follow> findFollowersByTailorId(@Param("tailorId") Long tailorId);
}