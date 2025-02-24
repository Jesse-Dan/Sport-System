package com.backend.golvia.app.specifications;

import org.springframework.data.jpa.domain.Specification;

import com.backend.golvia.app.entities.Fan;


public class FanSpecifications {
    
    public static Specification<Fan> hasCountry(String country) {
        return (root, query, criteriaBuilder) -> country == null ? null : criteriaBuilder.equal(root.get("country"), country);
    }

    public static Specification<Fan> hasFavoriteSport(String favoriteSport) {
        return (root, query, criteriaBuilder) -> favoriteSport == null ? null : criteriaBuilder.equal(root.get("favoriteSports"), favoriteSport);
    }

    public static Specification<Fan> hasFavoriteAthlete(String favoriteAthlete) {
        return (root, query, criteriaBuilder) -> favoriteAthlete == null ? null : criteriaBuilder.equal(root.get("favoriteAthletes"), favoriteAthlete);
    }

    public static Specification<Fan> hasUsername(String username) {
        return (root, query, criteriaBuilder) -> username == null ? null : criteriaBuilder.equal(root.get("username"), username);
    }

    public static Specification<Fan> hasCity(String city) {
        return (root, query, criteriaBuilder) -> city == null ? null : criteriaBuilder.equal(root.get("city"), city);
    }

    public static Specification<Fan> hasNotificationPreference(String notificationPreference) {
        return (root, query, criteriaBuilder) -> notificationPreference == null ? null : criteriaBuilder.equal(root.get("notificationPreferences"), notificationPreference);
    }

    public static Specification<Fan> hasPurchasedItem(String purchasedItem) {
        return (root, query, criteriaBuilder) -> purchasedItem == null ? null : criteriaBuilder.equal(root.get("purchasedItems"), purchasedItem);
    }
}
