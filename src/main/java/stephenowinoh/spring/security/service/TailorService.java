package stephenowinoh.spring.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import stephenowinoh.spring.security.DTO.TailorDTO;
import stephenowinoh.spring.security.mapper.TailorMapper;
import stephenowinoh.spring.security.repository.MyUserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TailorService {

    @Autowired
    private MyUserRepository myUserRepository;

    /**
     * Get all tailors
     * Used for: Dashboard display
     */
    public List<TailorDTO> getAllTailors() {
        return myUserRepository.findByRole("TAILOR").stream()
                .map(TailorMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get tailors by specialty
     * Used for: Filter buttons (Suits, Dresses, Traditional, etc.)
     */
    public List<TailorDTO> getTailorsBySpecialty(String specialty) {
        return myUserRepository.findByRoleAndSpecialty("TAILOR", specialty).stream()
                .map(TailorMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get tailors by nationality
     * Used for: Country filter
     */
    public List<TailorDTO> getTailorsByNationality(String nationality) {
        return myUserRepository.findByRoleAndNationality("TAILOR", nationality).stream()
                .map(TailorMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Search tailors by name, location, or nationality
     * Used for: Search bar
     */
    public List<TailorDTO> searchTailors(String query) {
        return myUserRepository.searchTailorsByQuery("TAILOR", query).stream()
                .map(TailorMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get tailor by ID
     * Used for: View tailor profile page
     */
    public TailorDTO getTailorById(Long id) {
        return myUserRepository.findById(id)
                .filter(user -> "TAILOR".equals(user.getRole()))
                .map(TailorMapper::toDTO)
                .orElse(null);
    }

    /**
     * Get total number of tailors
     * Used for: Statistics/dashboard stats
     */
    public long getTailorCount() {
        return myUserRepository.countByRole("TAILOR");
    }

    /**
     * Check if tailor exists
     * Used for: Validation before booking
     */
    public boolean tailorExists(Long id) {
        return myUserRepository.findById(id)
                .map(user -> "TAILOR".equals(user.getRole()))
                .orElse(false);
    }
}