package com.backend.golvia.app.repositories.profiles;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.backend.golvia.app.entities.Scout;

@Repository
public interface ScoutRepository extends JpaRepository<Scout, Long>, JpaSpecificationExecutor<Scout> {
    Optional<Scout> findByEmail(String email);

    @Query(value = "SELECT * FROM scouts WHERE country = :country", nativeQuery = true)
    List<Scout> findByCountry(@Param("country") String country);

    @Query(value = "SELECT * FROM scouts WHERE specialization = :specialization", nativeQuery = true)
    List<Scout> findBySpecialization(@Param("specialization") String specialization);

    @Query(value = "SELECT * FROM scouts WHERE affiliated_organization LIKE %:organization%", nativeQuery = true)
    List<Scout> findByAffiliatedOrganization(@Param("organization") String organization);

    @Query(value = "SELECT * FROM scouts WHERE scouting_experience > :years", nativeQuery = true)
    List<Scout> findByExperienceGreaterThan(@Param("years") int years);

    @Query(value = "SELECT * FROM scouts ORDER BY scouting_experience DESC LIMIT :limit", nativeQuery = true)
    List<Scout> findTopExperiencedScouts(@Param("limit") int limit);

    @Query(value = "SELECT * FROM scouts WHERE preferred_regions LIKE %:region%", nativeQuery = true)
    List<Scout> findByPreferredRegion(@Param("region") String region);

    @Query(value = "SELECT * FROM scouts WHERE certifications LIKE %:certification%", nativeQuery = true)
    List<Scout> findByCertification(@Param("certification") String certification);

    @Query(value = "SELECT * FROM scouts WHERE notable_talents LIKE %:talent%", nativeQuery = true)
    List<Scout> findByNotableTalent(@Param("talent") String talent);

    @Query(value = "SELECT COUNT(*) FROM scouts WHERE scouting_experience > :years", nativeQuery = true)
    int countByExperienceGreaterThan(@Param("years") int years);

    // Find scouts by scouting experience greater than a certain number of years
    List<Scout> findByScoutingExperienceYearsGreaterThan(Integer years);

    // Find top scouts ordered by scouting experience (limit is implemented via
    // pagination)
    @Query(value = "SELECT * FROM scouts ORDER BY scouting_experience_years DESC", nativeQuery = true)
    List<Scout> findTopByOrderByScoutingExperienceYearsDesc(int limit);

    @Query("SELECT s FROM Scout s " +
            "LEFT JOIN s.areasOfSpecialization specialization " +
            "LEFT JOIN s.regionsOfInterest region " +
            "WHERE LOWER(s.email) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(specialization) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(region) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Scout> searchScouts(@Param("query") String query);


    @Modifying
    @Query(value = "DELETE s1 FROM scouts s1 INNER JOIN scouts s2 ON s1.email = s2.email WHERE s1.id > s2.id", nativeQuery = true)
    void deleteDuplicateScoutsByEmail();

}