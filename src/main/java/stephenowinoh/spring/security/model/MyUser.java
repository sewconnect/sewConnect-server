package stephenowinoh.spring.security.model;

import jakarta.persistence.*;

@Entity
public class MyUser {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username; // This will be the email

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role; // "CLIENT", "TAILOR", or "ADMIN"

    // NEW FIELDS for SewStyles
    private String fullName;
    private String phoneNumber;

    // Tailor-specific fields (null for clients)
    private String specialty; // e.g., "Wedding & Evening Wear Specialist"
    private String location;  // e.g., "Westlands, Nairobi"

    private String nationality;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }
}