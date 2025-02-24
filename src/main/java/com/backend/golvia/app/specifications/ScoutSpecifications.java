package com.backend.golvia.app.specifications;

import org.springframework.data.jpa.domain.Specification;

import com.backend.golvia.app.entities.Scout;

public class ScoutSpecifications {

    public static Specification<Scout> hasCountry(String country) {
        return (root, query, criteriaBuilder) -> country == null ? null : criteriaBuilder.equal(root.get("country"), country);
    }

    public static Specification<Scout> hasSpecialization(String specialization) {
        return (root, query, criteriaBuilder) -> specialization == null ? null : criteriaBuilder.equal(root.get("specialization"), specialization);
    }

    public static Specification<Scout> hasAffiliatedOrganization(String organization) {
        return (root, query, criteriaBuilder) -> organization == null ? null : criteriaBuilder.equal(root.get("affiliatedOrganization"), organization);
    }

    public static Specification<Scout> hasExperienceGreaterThan(int years) {
        return (root, query, criteriaBuilder) -> years <= 0 ? null : criteriaBuilder.greaterThan(root.get("experience"), years);
    }

    public static Specification<Scout> hasPreferredRegion(String region) {
        return (root, query, criteriaBuilder) -> region == null ? null : criteriaBuilder.equal(root.get("preferredRegion"), region);
    }

    public static Specification<Scout> hasCertification(String certification) {
        return (root, query, criteriaBuilder) -> certification == null ? null : criteriaBuilder.equal(root.get("certification"), certification);
    }

    public static Specification<Scout> hasNotableTalent(String talent) {
        return (root, query, criteriaBuilder) -> talent == null ? null : criteriaBuilder.equal(root.get("notableTalent"), talent);
    }

    public static Specification<Scout> hasAffiliatedOrganizations(String organization) {
        return (root, query, criteriaBuilder) -> organization == null ? null : criteriaBuilder.equal(root.get("affiliatedOrganizations"), organization);
    }
}
