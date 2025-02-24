package com.backend.golvia.app.specifications;

import org.springframework.data.jpa.domain.Specification;

import com.backend.golvia.app.entities.Athlete;

public class AthleteSpecifications {

    public static Specification<Athlete> hasCountry(String country) {
        return (root, query, cb) -> country == null ? null : cb.equal(root.get("country"), country);
    }

    public static Specification<Athlete> hasExperienceGreaterThan(Integer years) {
        return (root, query, cb) -> years == null ? null : cb.greaterThan(root.get("yearsOfExperience"), years);
    }
}
