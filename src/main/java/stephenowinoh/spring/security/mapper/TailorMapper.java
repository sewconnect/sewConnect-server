package stephenowinoh.spring.security.mapper;

import stephenowinoh.spring.security.DTO.TailorDTO;
import stephenowinoh.spring.security.model.MyUser;

public class TailorMapper {

    // Convert MyUser to TailorDTO
    public static TailorDTO toDTO(MyUser user) {
        if (user == null) {
            return null;
        }

        TailorDTO dto = new TailorDTO();
        dto.setId(user.getId());
        dto.setFullName(user.getFullName());
        dto.setUsername(user.getUsername());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setSpecialty(user.getSpecialty());
        dto.setLocation(user.getLocation());
        dto.setNationality(user.getNationality());

        return dto;
    }

    // Optional: Convert TailorDTO to MyUser (if needed for updates)
    public static MyUser toEntity(TailorDTO dto) {
        if (dto == null) {
            return null;
        }

        MyUser user = new MyUser();
        user.setId(dto.getId());
        user.setFullName(dto.getFullName());
        user.setUsername(dto.getUsername());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setSpecialty(dto.getSpecialty());
        user.setLocation(dto.getLocation());
        user.setNationality(dto.getNationality());
        user.setRole("TAILOR");

        return user;
    }
}