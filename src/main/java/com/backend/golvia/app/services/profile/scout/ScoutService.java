package com.backend.golvia.app.services.profile.scout;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.backend.golvia.app.dtos.profile.ScoutUpdateDto;
import com.backend.golvia.app.dtos.profile.ScoutDTO;
import com.backend.golvia.app.entities.Athlete;
import com.backend.golvia.app.entities.Scout;
import com.backend.golvia.app.repositories.auth.UserRepository;
import com.backend.golvia.app.repositories.profiles.AthleteRepository;
import com.backend.golvia.app.repositories.profiles.ScoutRepository;
import com.backend.golvia.app.services.profile.asset.AssetService;
import com.backend.golvia.app.specifications.ScoutSpecifications;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class ScoutService {

    @Autowired
    private ScoutRepository scoutRepository;

    @Autowired
    private AthleteRepository athleteRepository;

    private final AssetService assetService;

    private final UserRepository userRepository;

    public ScoutService(AssetService assetService, UserRepository userRepository) {
        this.assetService = assetService;
        this.userRepository = userRepository;
    }

    // Finds scouts by various criteria with validation
    public List<Scout> findScoutsByCriteria(String country, String specialization, String organization, Integer years,
                                            String region, String certification, String talent) throws Exception {
        if (country == null || country.isEmpty()) throw new Exception("Country cannot be empty");
        if (specialization == null || specialization.isEmpty()) throw new Exception("Specialization cannot be empty");
        if (organization == null || organization.isEmpty()) throw new Exception("Organization cannot be empty");

        Specification<Scout> spec = Specification.where(ScoutSpecifications.hasCountry(country))
                .and(ScoutSpecifications.hasSpecialization(specialization))
                .and(ScoutSpecifications.hasAffiliatedOrganization(organization))
                .and(ScoutSpecifications.hasExperienceGreaterThan(years != null ? years : 0))
                .and(ScoutSpecifications.hasPreferredRegion(region))
                .and(ScoutSpecifications.hasCertification(certification))
                .and(ScoutSpecifications.hasNotableTalent(talent));

        return scoutRepository.findAll(spec);
    }

    // Fetch all scouts
    public List<ScoutDTO> findAll() {
        return scoutRepository.findAll().stream()
                .map(scout -> {
                    try {
                        return convertToDTO(scout);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }

    // Find scout by email with validation
    public ScoutDTO findByEmail(String email) throws Exception {
        if (email == null || email.isEmpty()) throw new Exception("Email cannot be empty");

        return scoutRepository.findByEmail(email).map(scout -> {
            try {
                return convertToDTO(scout);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).orElseThrow(() ->
                new RuntimeException("Scout not found with email: " + email));
    }

    // Get scouted athletes for a scout
    public List<Athlete> getScoutedAthletes(String email) throws Exception {
        if (email == null || email.isEmpty()) throw new Exception("Email cannot be empty");

        Optional<Scout> scoutOptional = scoutRepository.findByEmail(email);
        if (scoutOptional.isPresent()) {
            Scout scout = scoutOptional.get();
            List<Athlete> athleteList = new ArrayList<>();
            List<String> athleteEmails = scout.getScoutedAtlethes();

            for (String athleteEmail : athleteEmails) {
                athleteRepository.findByEmail(athleteEmail)
                        .ifPresent(athleteList::add);
            }
            return athleteList;
        } else {
            throw new Exception("Scout with email " + email + " not found");
        }
    }

    // Create a new scout
    public ScoutDTO create(ScoutDTO scoutDTO) throws Exception {
        if (scoutDTO == null || scoutDTO.getEmail() == null || scoutDTO.getEmail().isEmpty()) {
            throw new Exception("Scout data or email cannot be empty");
        }
        Scout scout = convertToEntity(scoutDTO);
        return convertToDTO(scoutRepository.save(scout));
    }

    // Update scout details
    public ScoutDTO update(String email, ScoutUpdateDto scoutDTO) throws Exception {
        if (email == null || email.isEmpty()) throw new Exception("Email cannot be empty");
        if (scoutDTO == null) throw new Exception("Scout update data cannot be null");

        assetService.updateAsset(userRepository.findByEmail(email), scoutDTO.getAsset());

        Scout scout = scoutRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Scout not found with email: " + email));

        scout.setUsername(scoutDTO.getUsername());
        scout.setCity(scoutDTO.getCity());
        scout.setScoutingExperienceYears(scoutDTO.getScoutingExperienceYears());
        scout.setNotableTalents(scoutDTO.getNotableTalents());
        scout.setAreasOfSpecialization(scoutDTO.getAreasOfSpecialization());
        scout.setAffiliatedOrganizations(scoutDTO.getAffiliatedOrganizations());
        scout.setScoutingRegions(scoutDTO.getScoutingRegions());
        scout.setCertifications(scoutDTO.getCertifications());
        scout.setRegionsOfInterest(scoutDTO.getRegionsOfInterest());
        scout.setScoutedAtlethes(scoutDTO.getScoutedAthletes());
        scout.setNotesOnAthletes(scoutDTO.getNotesOnAthletes());
        scout.setPosition(scoutDTO.getPosition());
        scout.setAgeGroup(scoutDTO.getAgeGroup());
        scout.setScoutingHistory(scoutDTO.getScoutingHistory());
        scout.setSports(scoutDTO.getSports());
        scout.setPhoneNumber(scoutDTO.getPhoneNumber());
        scout.setSocialMediaLinks(scoutDTO.getSocialMediaLinks());
        scout.setIsActive(scoutDTO.getIsActive());
        scout.setUpdatedAt(new Date());

        Scout updatedScout = scoutRepository.save(scout);
        return convertToDTO(updatedScout);
    }

    // Delete scout by ID with validation
    public boolean delete(Long id) throws Exception {
        if (id == null || id <= 0) throw new Exception("Invalid ID");
        if (scoutRepository.existsById(id)) {
            scoutRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Convert Entity to DTO
    private ScoutDTO convertToDTO(Scout scout) throws Exception {
        if (scout == null) throw new Exception("Scout entity cannot be null");

        ScoutDTO dto = new ScoutDTO();
        dto.setUsername(scout.getUsername());
        dto.setEmail(scout.getEmail());
        dto.setCity(scout.getCity());
        dto.setScoutingExperienceYears(scout.getScoutingExperienceYears());
        dto.setNotableTalents(scout.getNotableTalents());
        dto.setAreasOfSpecialization(scout.getAreasOfSpecialization());
        dto.setAffiliatedOrganizations(scout.getAffiliatedOrganizations());
        dto.setScoutingRegions(scout.getScoutingRegions());
        dto.setCertifications(scout.getCertifications());
        dto.setRegionsOfInterest(scout.getRegionsOfInterest());
        dto.setNotesOnAthletes(scout.getNotesOnAthletes());
        dto.setPosition(scout.getPosition());
        dto.setAgeGroup(scout.getAgeGroup());
        dto.setScoutingHistory(scout.getScoutingHistory());
        dto.setSports(scout.getSports());
        dto.setPhoneNumber(scout.getPhoneNumber());
        dto.setSocialMediaLinks(scout.getSocialMediaLinks());
        dto.setIsActive(scout.getIsActive());
        return dto;
    }

    // Convert DTO to Entity
    private Scout convertToEntity(ScoutDTO dto) throws Exception {
        if (dto == null || dto.getEmail() == null || dto.getEmail().isEmpty()) throw new Exception("Scout DTO or email cannot be empty");

        Scout scout = new Scout();
        scout.setUsername(dto.getUsername());
        scout.setEmail(dto.getEmail());
        scout.setCity(dto.getCity());
        scout.setScoutingExperienceYears(dto.getScoutingExperienceYears());
        scout.setNotableTalents(dto.getNotableTalents());
        scout.setAreasOfSpecialization(dto.getAreasOfSpecialization());
        scout.setAffiliatedOrganizations(dto.getAffiliatedOrganizations());
        scout.setScoutingRegions(dto.getScoutingRegions());
        scout.setCertifications(dto.getCertifications());
        scout.setRegionsOfInterest(dto.getRegionsOfInterest());
        scout.setNotesOnAthletes(dto.getNotesOnAthletes());
        scout.setPosition(dto.getPosition());
        scout.setAgeGroup(dto.getAgeGroup());
        scout.setScoutingHistory(dto.getScoutingHistory());
        scout.setSports(dto.getSports());
        scout.setPhoneNumber(dto.getPhoneNumber());
        scout.setSocialMediaLinks(dto.getSocialMediaLinks());
        scout.setIsActive(dto.getIsActive());
        return scout;
    }
}
