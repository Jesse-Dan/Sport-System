package com.backend.golvia.app.repositories.profiles;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.backend.golvia.app.entities.Athlete;

@Repository
public interface AthleteRepository extends JpaRepository<Athlete, Long>, JpaSpecificationExecutor<Athlete> {
    Optional<Athlete> findByEmail(String email);


    @Query(value = "SELECT * FROM athletes WHERE country = :country", nativeQuery = true)
    List<Athlete> findByCountry(@Param("country") String country);


    @Query(value = "SELECT * FROM athletes WHERE years_of_experience > :years", nativeQuery = true)
    List<Athlete> findAthletesWithExperienceGreaterThan(@Param("years") int years);

    @Query("SELECT a FROM Athlete a WHERE LOWER(a.biography) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(a.currentClub) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Athlete> searchAthletes(@Param("query") String query);

    @Modifying
    @Query(value = "DELETE a1 FROM athletes a1 INNER JOIN athletes a2 ON a1.email = a2.email WHERE a1.id > a2.id", nativeQuery = true)
    void deleteDuplicateAthletesByEmail();
}
