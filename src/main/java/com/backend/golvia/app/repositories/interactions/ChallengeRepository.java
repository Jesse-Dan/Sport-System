package com.backend.golvia.app.repositories.interactions;

import com.backend.golvia.app.entities.Challenge;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ChallengeRepository  extends JpaRepository<Challenge, Long> {

    @Query(value = "SELECT * FROM challenges WHERE title = :challengeType", nativeQuery = true)
    Optional<Challenge> findByChallengeType(@Param("challengeType") String challengeType);

    Page<Challenge> findById(Long id, Pageable pageable);
}
