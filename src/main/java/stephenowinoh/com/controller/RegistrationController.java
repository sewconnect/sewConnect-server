package stephenowinoh.com.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import stephenowinoh.com.model.MyUser;
import stephenowinoh.com.repository.MyUserRepository;

import java.util.Map;

@RestController
@RequestMapping("/register")
//@CrossOrigin(origins = "*") // Allow frontend to connect
@CrossOrigin(origins = {
        "http://localhost:5173",
        "https://sewconnectplatform.vercel.app"
})
public class RegistrationController {

    @Autowired
    private MyUserRepository myUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Register as CLIENT
    @PostMapping("/client")
    public ResponseEntity<?> registerClient(@RequestBody MyUser user) {
        // Check if username already exists
        if (myUserRepository.existsByUsername(user.getUsername())) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "Username already taken"));
        }

        // Set role to CLIENT
        user.setRole("CLIENT");

        // Encrypt password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Save user
        MyUser savedUser = myUserRepository.save(user);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of(
                        "message", "Client registered successfully",
                        "username", savedUser.getUsername(),
                        "role", savedUser.getRole()
                ));
    }

    // Register as TAILOR
    @PostMapping("/tailor")
    public ResponseEntity<?> registerTailor(@RequestBody MyUser user) {
        // Check if username already exists
        if (myUserRepository.existsByUsername(user.getUsername())) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "Username already taken"));
        }

        // Set role to TAILOR
        user.setRole("TAILOR");

        // Encrypt password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Save user
        MyUser savedUser = myUserRepository.save(user);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of(
                        "message", "Tailor registered successfully",
                        "username", savedUser.getUsername(),
                        "role", savedUser.getRole()
                ));
    }
}