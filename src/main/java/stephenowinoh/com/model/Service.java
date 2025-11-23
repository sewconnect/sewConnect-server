package stephenowinoh.com.model;


import jakarta.persistence.*;

@Entity
@Table(name = "services")
public class Service {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tailor_id", nullable = false)
    private MyUser tailor; // The tailor who offers this service

    @Column(nullable = false)
    private String serviceName; // e.g., "Custom Suits", "Wedding Dresses", "Alterations"

    @Column(nullable = false)
    private Double minPrice; // Minimum price for this service

    @Column(nullable = false)
    private Double maxPrice; // Maximum price for this service

    @Column(nullable = false, length = 3)
    private String currency; // e.g., "KES", "TZS", "USD", "NGN"

    @Column(length = 500)
    private String description; // Optional description of the service

    @Column(nullable = false)
    private Boolean isActive = true; // To soft delete services

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MyUser getTailor() {
        return tailor;
    }

    public void setTailor(MyUser tailor) {
        this.tailor = tailor;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public Double getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(Double minPrice) {
        this.minPrice = minPrice;
    }

    public Double getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(Double maxPrice) {
        this.maxPrice = maxPrice;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}
