package stephenowinoh.spring.security.DTO;

public class ProfileUpdateRequest {

    private String fullName;
    private String phoneNumber;
    private String location;
    private String specialty;
    private String nationality;
    private String bio;

    // Constructors
    public ProfileUpdateRequest() {}

    public ProfileUpdateRequest(String fullName, String phoneNumber, String location,
                                String specialty, String nationality, String bio) {
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.location = location;
        this.specialty = specialty;
        this.nationality = nationality;
        this.bio = bio;
    }

    // Getters and Setters
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}
