package stephenowinoh.spring.security.DTO;



public class ServiceDTO {
    private Long id;
    private String serviceName;
    private Double minPrice;
    private Double maxPrice;
    private String currency;
    private String description;
    private String priceRange; // Formatted string like "KES 5,000 - 15,000"

    // Constructors
    public ServiceDTO() {}

    public ServiceDTO(Long id, String serviceName, Double minPrice, Double maxPrice,
                      String currency, String description) {
        this.id = id;
        this.serviceName = serviceName;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.currency = currency;
        this.description = description;
        this.priceRange = formatPriceRange(currency, minPrice, maxPrice);
    }

    // Helper method to format price range
    private String formatPriceRange(String currency, Double min, Double max) {
        return String.format("%s %,.0f - %,.0f", currency, min, max);
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
        updatePriceRange();
    }

    public Double getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(Double maxPrice) {
        this.maxPrice = maxPrice;
        updatePriceRange();
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
        updatePriceRange();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPriceRange() {
        return priceRange;
    }

    public void setPriceRange(String priceRange) {
        this.priceRange = priceRange;
    }

    private void updatePriceRange() {
        if (currency != null && minPrice != null && maxPrice != null) {
            this.priceRange = formatPriceRange(currency, minPrice, maxPrice);
        }
    }
}