package com.backend.golvia.app.services.profile.fan;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.backend.golvia.app.dtos.profile.FanUpdateDto;
import com.backend.golvia.app.entities.UserActivity;
import com.backend.golvia.app.repositories.activities.UserActivityRepository;
import com.backend.golvia.app.repositories.auth.UserRepository;
import com.backend.golvia.app.services.profile.asset.AssetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import com.backend.golvia.app.dtos.profile.FanDTO;
import com.backend.golvia.app.entities.Fan;
import com.backend.golvia.app.repositories.profiles.FanRepository;
import com.backend.golvia.app.specifications.FanSpecifications;
import org.springframework.util.StringUtils;

@Service
public class FanService {

    @Autowired
    private FanRepository fanRepository;

    private final AssetService assetService;

    private final UserRepository userRepository;

    private final UserActivityRepository userActivityRepository;

    public FanService(AssetService assetService, UserRepository userRepository, UserActivityRepository userActivityRepository) {
        this.assetService = assetService;
        this.userRepository = userRepository;
        this.userActivityRepository = userActivityRepository;
    }

    // Create a new Fan with validations
    public Fan createFan(FanDTO fanDTO) throws Exception {
        // Validate input
        if (fanDTO == null) {
            throw new Exception("FanDTO cannot be null");
        }

        if (!isValidEmail(fanDTO.getEmail())) {
            throw new Exception("Invalid email format");
        }

        if (fanRepository.existsByEmail(fanDTO.getEmail())) {
            throw new Exception("A fan with this email already exists");
        }

        Fan fan = new Fan();
        // Map DTO fields to Fan entity
        fan.setEmail(fanDTO.getEmail());
        fan.setCity(fanDTO.getCity());
        fan.setFavoriteSports(fanDTO.getFavoriteSports());
        fan.setFavoriteAthletes(fanDTO.getFavoriteAthletes());
        fan.setNotificationPreferences(fanDTO.getNotificationPreferences());
        fan.setInteractions(fanDTO.getInteractions());
        fan.setPurchasedItems(fanDTO.getPurchasedItems());

        return fanRepository.save(fan);

    }

    public Optional<Fan> getFanById(Long id) throws Exception {
        if (id == null || id <= 0) {
            throw new Exception("Invalid fan ID");
        }

        return fanRepository.findById(id);
    }

    public Fan updateFan(String email, FanUpdateDto fanDTO) throws Exception {
        if (fanDTO == null) {
            throw new Exception("FanUpdateDto cannot be null");
        }

        if (!isValidEmail(email)) {
            throw new Exception("Invalid email format");
        }

        Fan fan = fanRepository.findByEmail(email);
        if (fan == null) {
            throw new Exception("Fan not found with email: " + email);
        }

        assetService.updateAsset(userRepository.findByEmail(email), fanDTO.getAsset());

        // Update fields
        fan.setCity(fanDTO.getCity());
        fan.setFavoriteSports(fanDTO.getFavoriteSports());
        fan.setFavoriteAthletes(fanDTO.getFavoriteAthletes());
        fan.setNotificationPreferences(fanDTO.getNotificationPreferences());
        fan.setInteractions(fanDTO.getInteractions());
        fan.setPurchasedItems(fanDTO.getPurchasedItems());

        return fanRepository.save(fan);
    }

    // Delete a Fan with validation
    public void deleteFan(Long id) throws Exception {
        if (id == null || id <= 0) {
            throw new Exception("Invalid fan ID");
        }

        if (!fanRepository.existsById(id)) {
            throw new Exception("Fan not found with ID: " + id);
        }

        fanRepository.deleteById(id);
    }

    // Find Fans by various criteria using Specifications with validation
    public List<Fan> findFansByCriteria(String country, String favoriteSport, String favoriteAthlete, String username,
                                        String city, String notificationPreference, String purchasedItem, int limit) throws Exception {

        if (limit <= 0) {
            throw new Exception("Limit must be a positive integer");
        }

        Specification<Fan> spec = Specification.where(FanSpecifications.hasCountry(country))
                .and(FanSpecifications.hasFavoriteSport(favoriteSport))
                .and(FanSpecifications.hasFavoriteAthlete(favoriteAthlete))
                .and(FanSpecifications.hasUsername(username))
                .and(FanSpecifications.hasCity(city))
                .and(FanSpecifications.hasNotificationPreference(notificationPreference))
                .and(FanSpecifications.hasPurchasedItem(purchasedItem));

        List<Fan> fans = fanRepository.findAll(spec);

        // Limit the results
        if (fans.size() > limit) {
            return fans.subList(0, limit);
        }

        return fans;
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);

        return matcher.matches();
    }
}
