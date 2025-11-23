package stephenowinoh.com.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stephenowinoh.com.DTO.ServiceDTO;
import stephenowinoh.com.model.MyUser;
import stephenowinoh.com.repository.MyUserRepository;
import stephenowinoh.com.repository.ServiceRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TailorServiceManager {

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private MyUserRepository myUserRepository;

    /**
     * Get all services for a tailor
     */
    public List<ServiceDTO> getServicesByTailorId(Long tailorId) {
        return serviceRepository.findByTailorIdAndIsActiveTrue(tailorId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Create a new service for a tailor
     */
    @Transactional
    public ServiceDTO createService(Long tailorId, ServiceDTO serviceDTO) {
        MyUser tailor = myUserRepository.findById(tailorId)
                .orElseThrow(() -> new RuntimeException("Tailor not found"));

        if (!"TAILOR".equals(tailor.getRole())) {
            throw new RuntimeException("User is not a tailor");
        }

        stephenowinoh.com.model.Service service =
                new stephenowinoh.com.model.Service();
        service.setTailor(tailor);
        service.setServiceName(serviceDTO.getServiceName());
        service.setMinPrice(serviceDTO.getMinPrice());
        service.setMaxPrice(serviceDTO.getMaxPrice());
        service.setCurrency(serviceDTO.getCurrency());
        service.setDescription(serviceDTO.getDescription());
        service.setIsActive(true);

        stephenowinoh.com.model.Service saved = serviceRepository.save(service);
        return toDTO(saved);
    }

    /**
     * Update an existing service
     */
    @Transactional
    public ServiceDTO updateService(Long serviceId, ServiceDTO serviceDTO) {
        stephenowinoh.com.model.Service service =
                serviceRepository.findById(serviceId)
                        .orElseThrow(() -> new RuntimeException("Service not found"));

        service.setServiceName(serviceDTO.getServiceName());
        service.setMinPrice(serviceDTO.getMinPrice());
        service.setMaxPrice(serviceDTO.getMaxPrice());
        service.setCurrency(serviceDTO.getCurrency());
        service.setDescription(serviceDTO.getDescription());

        stephenowinoh.com.model.Service updated = serviceRepository.save(service);
        return toDTO(updated);
    }

    /**
     * Delete a service (soft delete)
     */
    @Transactional
    public void deleteService(Long serviceId) {
        stephenowinoh.com.model.Service service =
                serviceRepository.findById(serviceId)
                        .orElseThrow(() -> new RuntimeException("Service not found"));

        service.setIsActive(false);
        serviceRepository.save(service);
    }

    /**
     * Convert Service entity to DTO
     */
    private ServiceDTO toDTO(stephenowinoh.com.model.Service service) {
        return new ServiceDTO(
                service.getId(),
                service.getServiceName(),
                service.getMinPrice(),
                service.getMaxPrice(),
                service.getCurrency(),
                service.getDescription()
        );
    }
}