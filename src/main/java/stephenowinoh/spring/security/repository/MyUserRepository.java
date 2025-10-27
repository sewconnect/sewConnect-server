package stephenowinoh.spring.security.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import stephenowinoh.spring.security.model.MyUser;

import java.util.List;
import java.util.Optional;

public interface MyUserRepository extends JpaRepository<MyUser, Long> {

    // ========== AUTHENTICATION ==========
    Optional<MyUser> findByUsername(String username);
    boolean existsByUsername(String username);

    // ========== TAILOR QUERIES =
    List<MyUser> findByRole(String role);

    List<MyUser> findByRoleAndSpecialty(String role, String specialty);

    List<MyUser> findByRoleAndNationality(String role, String nationality);

    Optional<MyUser> findByEmail(String email);
    boolean existsByEmail(String email);

    @Query("SELECT u FROM MyUser u WHERE u.role = :role AND " +
            "(LOWER(u.fullName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(u.location) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(u.nationality) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<MyUser> searchTailorsByQuery(@Param("role") String role, @Param("query") String query);

    long countByRole(String role);
}


