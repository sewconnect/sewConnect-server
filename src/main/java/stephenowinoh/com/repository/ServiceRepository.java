package stephenowinoh.com.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stephenowinoh.com.model.MyUser;
import stephenowinoh.com.model.Service;

import java.util.List;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Long> {

    // Find all services by tailor
    List<Service> findByTailorAndIsActiveTrue(MyUser tailor);

    // Find services by tailor ID
    List<Service> findByTailorIdAndIsActiveTrue(Long tailorId);

    // Count active services for a tailor
    long countByTailorAndIsActiveTrue(MyUser tailor);

    // Check if tailor has reached service limit (optional)
    long countByTailorIdAndIsActiveTrue(Long tailorId);
}
