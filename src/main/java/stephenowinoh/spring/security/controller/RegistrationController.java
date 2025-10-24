package stephenowinoh.spring.security.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import stephenowinoh.spring.security.model.MyUser;
import stephenowinoh.spring.security.repository.MyUserRepository;

import java.util.Map;

@RestController
public class RegistrationController {

    @Autowired
    private MyUserRepository myUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @PostMapping("/register/user")
    public ResponseEntity<?> createUser(@RequestBody MyUser user) {
        if (myUserRepository.existsByUsername(user.getUsername())) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT) // 409 Conflict
                    .body(Map.of("message", "Username already taken"));
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        MyUser savedUser = myUserRepository.save(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

}
