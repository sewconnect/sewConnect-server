package stephenowinoh.spring.security.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import stephenowinoh.spring.security.DTO.ServiceDTO;
import stephenowinoh.spring.security.service.TailorServiceManager;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/services")
@CrossOrigin(origins = "*")
public class ServiceController {

    @Autowired
    private TailorServiceManager tailorServiceManager;

    /**
     * GET /api/services/tailor/{tailorId}
     * Get all services for a specific tailor
     */
    @GetMapping("/tailor/{tailorId}")
    public ResponseEntity<List<ServiceDTO>> getServicesByTailor(@PathVariable Long tailorId) {
        List<ServiceDTO> services = tailorServiceManager.getServicesByTailorId(tailorId);
        return ResponseEntity.ok(services);
    }

    /**
     * POST /api/services/tailor/{tailorId}
     * Create a new service for a tailor
     */
    @PostMapping("/tailor/{tailorId}")
    public ResponseEntity<?> createService(
            @PathVariable Long tailorId,
            @RequestBody ServiceDTO serviceDTO) {
        try {
            ServiceDTO created = tailorServiceManager.createService(tailorId, serviceDTO);
            return ResponseEntity.ok(created);
        } catch (RuntimeException e) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * PUT /api/services/{serviceId}
     * Update an existing service
     */
    @PutMapping("/{serviceId}")
    public ResponseEntity<?> updateService(
            @PathVariable Long serviceId,
            @RequestBody ServiceDTO serviceDTO) {
        try {
            ServiceDTO updated = tailorServiceManager.updateService(serviceId, serviceDTO);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * DELETE /api/services/{serviceId}
     * Delete a service (soft delete)
     */
    @DeleteMapping("/{serviceId}")
    public ResponseEntity<?> deleteService(@PathVariable Long serviceId) {
        try {
            tailorServiceManager.deleteService(serviceId);
            return ResponseEntity.ok(Map.of("message", "Service deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }
}