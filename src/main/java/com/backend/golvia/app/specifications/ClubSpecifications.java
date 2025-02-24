package com.backend.golvia.app.specifications;

import org.springframework.data.jpa.domain.Specification;

import com.backend.golvia.app.entities.Club;

public class ClubSpecifications {

    public static Specification<Club> hasCountry(String country) {
        return (root, query, cb) -> country == null ? null : cb.equal(root.get("country"), country);
    }

    public static Specification<Club> hasCompetitionLevel(String level) {
        return (root, query, cb) -> level == null ? null : cb.equal(root.get("competitionLevel"), level);
    }

    public static Specification<Club> hasRecruitmentArea(String area) {
        return (root, query, cb) -> area == null ? null : cb.equal(root.get("recruitmentArea"), area);
    }

    public static Specification<Club> hasCity(String city) {
        return (root, query, cb) -> city == null ? null : cb.equal(root.get("city"), city);
    }

    public static Specification<Club> containsName(String name) {
        return (root, query, cb) -> name == null ? null : cb.like(root.get("name"), "%" + name + "%");
    }

    public static Specification<Club> containsWebsite(String url) {
        return (root, query, cb) -> url == null ? null : cb.like(root.get("website"), "%" + url + "%");
    }
}
