package com.backend.golvia.app.services.profile.club;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.backend.golvia.app.dtos.profile.ClubUpdateDto;
import com.backend.golvia.app.repositories.auth.UserRepository;
import com.backend.golvia.app.services.profile.asset.AssetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.backend.golvia.app.dtos.profile.ClubDTO;
import com.backend.golvia.app.entities.Club;
import com.backend.golvia.app.repositories.profiles.ClubRepository;
import com.backend.golvia.app.specifications.ClubSpecifications;

@Service
public class ClubService {

    @Autowired
    private ClubRepository clubRepository;

    private final AssetService assetService;

    private final UserRepository userRepository;

    public ClubService(AssetService assetService, UserRepository userRepository) {
        this.assetService = assetService;
        this.userRepository = userRepository;
    }

    // Helper method to convert a Club entity to ClubDTO
    private ClubDTO convertToDTO(Club club) {
        ClubDTO dto = new ClubDTO();
        dto.setId(club.getId());
        dto.setClubName(club.getClubName());
        dto.setCountry(club.getCountry());
        dto.setCity(club.getCity());
        dto.setCompetitionLevel(club.getCompetitionLevel());
        dto.setContactEmail(club.getContactEmail());
        dto.setContactPersonName(club.getContactPersonName());
        dto.setContactPhone(club.getContactPhone());
        dto.setWebsite(club.getWebsite());
        dto.setSocialLinks(club.getSocialLinks());
        dto.setRecruitmentAreas(club.getRecruitmentAreas());
        dto.setPlayerType(club.getPlayerType());
        dto.setTeamLogoUrl(club.getTeamLogoUrl());
        dto.setPlayers(club.getPlayers());
        dto.setClubAchievements(club.getClubAchievements());
        dto.setClubVacancies(club.getClubVacancies());
        return dto;
    }

    // Save a new Club with validation
    public ClubDTO saveClub(ClubDTO dto) throws Exception {
        if (dto == null) {
            throw new Exception("ClubDTO cannot be null");
        }

        if (!isValidEmail(dto.getContactEmail())) {
            throw new Exception("Invalid email format");
        }

        if (clubRepository.existsByContactEmail(dto.getContactEmail())) {
            throw new Exception("A club with this email already exists");
        }

        Club club = new Club();
        club.setClubName(dto.getClubName());
        club.setCountry(dto.getCountry());
        club.setCity(dto.getCity());
        club.setCompetitionLevel(dto.getCompetitionLevel());
        club.setContactEmail(dto.getContactEmail());
        club.setContactPersonName(dto.getContactPersonName());
        club.setContactPhone(dto.getContactPhone());
        club.setWebsite(dto.getWebsite());
        club.setSocialLinks(dto.getSocialLinks());
        club.setRecruitmentAreas(dto.getRecruitmentAreas());
        club.setPlayerType(dto.getPlayerType());
        club.setTeamLogoUrl(dto.getTeamLogoUrl());
        club.setPlayers(dto.getPlayers());
        club.setClubAchievements(dto.getClubAchievements());
        club.setClubVacancies(dto.getClubVacancies());
        Club savedClub = clubRepository.save(club);
        return convertToDTO(savedClub);
    }

    // Update an existing Club with validation
    public Optional<ClubDTO> updateClub(String email, ClubUpdateDto club) throws Exception {

        if (club == null) {
            throw new Exception("ClubUpdateDto cannot be null");
        }

        if(clubRepository.findByContactEmail(email).getClubName().equals(club.getClubName())){
            throw new Exception("Club with specified club name already exists");
        }

        if (!isValidEmail(email)) {
            throw new Exception("Invalid email format");
        }

        Optional<Club> optionalClub = Optional.ofNullable(clubRepository.findByContactEmail(email));
        if (!optionalClub.isPresent()) {
            throw new Exception("No club found with the given contact email");
        }

        assetService.updateAsset(userRepository.findByEmail(email), club.getAsset());

        Club dto = optionalClub.get();
        dto.setId(club.getId());
        dto.setClubName(club.getClubName());
        dto.setCountry(club.getCountry());
        dto.setCity(club.getCity());
        dto.setCompetitionLevel(club.getCompetitionLevel());
        dto.setContactEmail(club.getContactEmail());
        dto.setContactPersonName(club.getContactPersonName());
        dto.setContactPhone(club.getContactPhone());
        dto.setWebsite(club.getWebsite());
        dto.setSocialLinks(club.getSocialLinks());
        dto.setRecruitmentAreas(club.getRecruitmentAreas());
        dto.setPlayerType(club.getPlayerType());
        dto.setTeamLogoUrl(club.getTeamLogoUrl());
        dto.setPlayers(club.getPlayers());
        dto.setClubAchievements(club.getClubAchievements());
        dto.setClubVacancies(club.getClubVacancies());
        Club updatedClub = clubRepository.save(dto);
        return Optional.of(convertToDTO(updatedClub));
    }

    // Delete a Club with validation
    public void deleteClub(Long id) throws Exception {
        if (id == null || id <= 0) {
            throw new Exception("Invalid club ID");
        }

        if (!clubRepository.existsById(id)) {
            throw new Exception("Club not found with ID: " + id);
        }

        clubRepository.deleteById(id);
    }

    public List<ClubDTO>  findAllClubs() throws Exception {

      List<Club> clubs  =clubRepository.findAll();
      List<ClubDTO> dtos = new ArrayList<>(List.of());

        if (clubs.isEmpty()) {
            throw new Exception("No clubs found");
        }

        clubs.forEach(club -> {
            dtos.add(convertToDTO(club));
        });

        return  dtos;
    }

    // Check if Club exists by ID
    public boolean existsById(Long id) throws Exception {
        if (id == null || id <= 0) {
            throw new Exception("Invalid club ID");
        }
        return clubRepository.existsById(id);
    }

    // Find Clubs by various criteria with validation
    public List<ClubDTO> findClubsByCriteria(String country, String level, String recruitmentArea, String city,
                                             String name, String website) throws Exception {

        if (country == null && level == null && recruitmentArea == null && city == null && name == null && website == null) {
            throw new Exception("At least one search criterion must be provided");
        }

        Specification<Club> spec = Specification.where(ClubSpecifications.hasCountry(country))
                .and(ClubSpecifications.hasCompetitionLevel(level))
                .and(ClubSpecifications.hasRecruitmentArea(recruitmentArea))
                .and(ClubSpecifications.hasCity(city))
                .and(ClubSpecifications.containsName(name))
                .and(ClubSpecifications.containsWebsite(website));

        List<Club> clubs = clubRepository.findAll(spec);
        return clubs.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);

        return matcher.matches();
    }
}
