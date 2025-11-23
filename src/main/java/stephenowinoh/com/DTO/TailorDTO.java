package stephenowinoh.com.DTO;

public class TailorDTO {
    private Long id;
    private String fullName;
    private String username;
    private String phoneNumber;
    private String specialty;
    private String location;
    private String nationality;

    // Constructors
    public TailorDTO() {
    }

    public TailorDTO(Long id, String fullName, String username, String phoneNumber,
                     String specialty, String location, String nationality) {
        this.id = id;
        this.fullName = fullName;
        this.username = username;
        this.phoneNumber = phoneNumber;
        this.specialty = specialty;
        this.location = location;
        this.nationality = nationality;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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
