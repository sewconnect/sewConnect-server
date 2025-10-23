package stephenowinoh.spring.security.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import stephenowinoh.spring.security.model.MyUser;

import java.util.Optional;

public interface MyUserRepository extends JpaRepository<MyUser,Long> {

    //getting user by username....

    Optional<MyUser> findByUsername(String username);
}
