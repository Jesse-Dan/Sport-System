package com.backend.golvia.app.repositories.activities;

import com.backend.golvia.app.entities.ActivityStats;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ActivityStatsRepository extends JpaRepository<ActivityStats, Long> {

    boolean existsByUserEmail(String email);

    void deleteByUserEmail(String email);

    Optional<ActivityStats> findByUserEmail(String email);
}
