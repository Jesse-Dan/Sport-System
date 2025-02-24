package com.backend.golvia.app.services.profile.athlete;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.backend.golvia.app.dtos.profile.AthleteUpdateDto;
import com.backend.golvia.app.repositories.auth.UserRepository;
import com.backend.golvia.app.services.profile.asset.AssetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.golvia.app.dtos.profile.AthleteDto;
import com.backend.golvia.app.entities.Athlete;
import com.backend.golvia.app.repositories.profiles.AthleteRepository;
import com.backend.golvia.app.specifications.AthleteSpecifications;
import org.springframework.util.StringUtils;

@Service
public class AthleteService {

    @Autowired
    private final AthleteRepository athleteRepository;

    private final AssetService assetService;

    private final UserRepository userRepository;

    public AthleteService(AthleteRepository athleteRepository, AssetService assetService, UserRepository userRepository) {
        this.athleteRepository = athleteRepository;
        this.assetService = assetService;
        this.userRepository = userRepository;
    }

    @Transactional
    public AthleteDto createAthlete(AthleteDto athleteDto) throws Exception {
        {// Validation for required fields
            if (athleteDto == null) {
                throw new Exception("Payload is required");
            }
            if (athleteDto.getYearsOfExperience() == null || athleteDto.getYearsOfExperience() < 0) {
                throw new Exception("Valid years of experience is required");
            }

            // Ensure email is unique
            Optional<Athlete> existingAthlete = athleteRepository.findByEmail(athleteDto.getEmail());
            if (existingAthlete.isPresent()) {
                throw new Exception("Athlete with this email already exists");
            }

            Athlete athlete = convertToEntity(athleteDto);
            Athlete savedAthlete = athleteRepository.save(athlete);
            return convertToDto(savedAthlete);
        }
    }

    public Optional<AthleteDto> getAthleteById(String email) throws Exception {
        if (!StringUtils.hasText(email)) {
            throw new Exception("Email must be provided");
        }
        return athleteRepository.findByEmail(email)
                .map(this::convertToDto);
    }

    @Transactional
    public void deleteAthlete(Long id) throws Exception {
        if (id == null || id <= 0) {
            throw new Exception("Invalid athlete ID");
        }
        if (!athleteRepository.existsById(id)) {
            throw new Exception("Athlete not found");
        }
        athleteRepository.deleteById(id);
    }

    public List<AthleteDto> getAthletesByCountryAndExperience(String country, Integer years) throws Exception {
        // Validation checks
        if (years != null && years < 0) {
            throw new Exception("Years of experience cannot be negative");
        }

        Specification<Athlete> spec = Specification.where(AthleteSpecifications.hasCountry(country))
                .and(AthleteSpecifications.hasExperienceGreaterThan(years));

        List<Athlete> athletes = athleteRepository.findAll(spec);

        return athletes.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // New Update Method
    @Transactional
    public Optional<AthleteUpdateDto> updateAthlete(String email, AthleteUpdateDto athleteDto) throws Exception {
        if (!StringUtils.hasText(email)) {
            throw new Exception("Email must be provided");
        }
        if (athleteDto == null) {
            throw new Exception("Athlete update data is required");
        }

        assetService.updateAsset(userRepository.findByEmail(email), athleteDto.getAsset());

        return athleteRepository.findByEmail(email)
                .map(existingAthlete -> {
                    existingAthlete.setAddress(athleteDto.getAddress());
                    existingAthlete.setDateOfBirth(athleteDto.getDateOfBirth());
                    existingAthlete.setYearsOfExperience(athleteDto.getYearsOfExperience());
                    existingAthlete.setHeight(athleteDto.getHeight());
                    existingAthlete.setWeight(athleteDto.getWeight());
                    existingAthlete.setBiography(athleteDto.getBiography());
                    existingAthlete.setCurrentClub(athleteDto.getCurrentClub());
                    existingAthlete.setPreferredPosition(athleteDto.getPreferredPosition());
                    existingAthlete.setPreferredFoot(athleteDto.getPreferredFoot());
                    existingAthlete.setPreferredClub(athleteDto.getPreferredClub());
                    existingAthlete.setProfession(athleteDto.getProfession());

                    // Save the updated athlete
                    Athlete updatedAthlete = athleteRepository.save(existingAthlete);
                    return convertToUpdateDto(updatedAthlete);
                });
    }

    // Conversion from DTO to Entity
    private Athlete convertToEntity(AthleteDto dto) {
        Athlete athlete = new Athlete();
        athlete.setId(dto.getId());
        athlete.setAddress(dto.getAddress());
        athlete.setDateOfBirth(dto.getDateOfBirth());
        athlete.setYearsOfExperience(dto.getYearsOfExperience());
        athlete.setHeight(dto.getHeight());
        athlete.setWeight(dto.getWeight());
        athlete.setBiography(dto.getBiography());
        athlete.setCurrentClub(dto.getCurrentClub());
        athlete.setPreferredPosition(dto.getPreferredPosition());
        athlete.setPreferredFoot(dto.getPreferredFoot());
        athlete.setPreferredClub(dto.getPreferredClub());
        athlete.setEmail(dto.getEmail());
        athlete.setProfession(dto.getProfession());
        return athlete;
    }

    // Conversion from Entity to DTO
    private AthleteDto convertToDto(Athlete athlete) {
        AthleteDto dto = new AthleteDto();
        dto.setId(athlete.getId());
        dto.setAddress(athlete.getAddress());
        dto.setDateOfBirth(athlete.getDateOfBirth());
        dto.setYearsOfExperience(athlete.getYearsOfExperience());
        dto.setHeight(athlete.getHeight());
        dto.setWeight(athlete.getWeight());
        dto.setBiography(athlete.getBiography());
        dto.setCurrentClub(athlete.getCurrentClub());
        dto.setPreferredPosition(athlete.getPreferredPosition());
        dto.setPreferredFoot(athlete.getPreferredFoot());
        dto.setPreferredClub(athlete.getPreferredClub());
        dto.setEmail(athlete.getEmail());
        dto.setProfession(athlete.getProfession());
        return dto;
    }

    // Conversion from Entity to Update DTO
    private AthleteUpdateDto convertToUpdateDto(Athlete athlete) {
        AthleteUpdateDto dto = new AthleteUpdateDto();

        dto.setAddress(athlete.getAddress());
        dto.setDateOfBirth(athlete.getDateOfBirth());
        dto.setYearsOfExperience(athlete.getYearsOfExperience());
        dto.setHeight(athlete.getHeight());
        dto.setWeight(athlete.getWeight());
        dto.setBiography(athlete.getBiography());
        dto.setCurrentClub(athlete.getCurrentClub());
        dto.setPreferredPosition(athlete.getPreferredPosition());
        dto.setPreferredFoot(athlete.getPreferredFoot());
        dto.setPreferredClub(athlete.getPreferredClub());
        dto.setProfession(athlete.getProfession());

        return dto;
    }
}
