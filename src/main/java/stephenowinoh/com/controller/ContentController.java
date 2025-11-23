package stephenowinoh.com.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import stephenowinoh.com.MyUserDetailsService;
import stephenowinoh.com.login.LoginForm;
import stephenowinoh.com.model.MyUser;
import stephenowinoh.com.repository.MyUserRepository;
import stephenowinoh.com.token.JwtService;

import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class ContentController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private MyUserDetailsService myUserDetailsService;

    @Autowired
    private MyUserRepository myUserRepository;

    @GetMapping("/home")
    public String handleWelcome() {
        return "Welcome to SewStyles - Connect with Professional Tailors";
    }

    @GetMapping("/admin/home")
    public String handleAdminHome(){
        return "Admin Dashboard - Manage Platform";
    }

    @GetMapping("/client/home")
    public String handleClientHome(){
        return "Client Dashboard - Find Your Perfect Tailor";
    }

    @GetMapping("/tailor/home")
    public String handleTailorHome(){
        return "Tailor Dashboard - Manage Your Bookings";
    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticateAndGetToken(@RequestBody LoginForm loginForm){
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginForm.username(),
                            loginForm.password()
                    )
            );

            if (authentication.isAuthenticated()) {
                var userDetails = myUserDetailsService.loadUserByUsername(loginForm.username());

                MyUser user = myUserRepository.findByUsername(loginForm.username())
                        .orElseThrow(() -> new UsernameNotFoundException("User not found"));

                // CRITICAL FIX: Pass userId to JWT generation
                String token = jwtService.generateToken(userDetails, user.getId());

                return ResponseEntity.ok(Map.of(
                        "token", token,
                        "id", user.getId(),
                        "username", user.getUsername(),
                        "role", user.getRole(),
                        "fullName", user.getFullName() != null ? user.getFullName() : "",
                        "message", "Login successful"
                ));
            } else {
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "Invalid credentials"));
            }

        } catch (UsernameNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "User not found"));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid username or password"));
        }
    }
}