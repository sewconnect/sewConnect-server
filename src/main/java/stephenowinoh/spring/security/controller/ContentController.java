package stephenowinoh.spring.security.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import stephenowinoh.spring.security.MyUserDetailsService;
import stephenowinoh.spring.security.login.LoginForm;
import stephenowinoh.spring.security.model.MyUser;
import stephenowinoh.spring.security.repository.MyUserRepository;
import stephenowinoh.spring.security.token.JwtService;

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

    // Public home page
    @GetMapping("/home")
    public String handleWelcome() {
        return "Welcome to SewStyles - Connect with Professional Tailors";
    }

    // Admin dashboard
    @GetMapping("/admin/home")
    public String handleAdminHome(){
        return "Admin Dashboard - Manage Platform";
    }

    // Client dashboard
    @GetMapping("/client/home")
    public String handleClientHome(){
        return "Client Dashboard - Find Your Perfect Tailor";
    }

    // Tailor dashboard
    @GetMapping("/tailor/home")
    public String handleTailorHome(){
        return "Tailor Dashboard - Manage Your Bookings";
    }

    // Login endpoint - Returns JWT token with user details
    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticateAndGetToken(@RequestBody LoginForm loginForm){
        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginForm.username(),
                            loginForm.password()
                    )
            );

            if (authentication.isAuthenticated()) {
                // Load user details
                var userDetails = myUserDetailsService.loadUserByUsername(loginForm.username());

                // Get full user entity from database to access id and role
                MyUser user = myUserRepository.findByUsername(loginForm.username())
                        .orElseThrow(() -> new UsernameNotFoundException("User not found"));

                // Generate JWT token
                String token = jwtService.generateToken(userDetails);

                // Return token with complete user info
                return ResponseEntity.ok(Map.of(
                        "token", token,
                        "id", user.getId(),
                        "username", user.getUsername(),
                        "role", user.getRole(),
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
