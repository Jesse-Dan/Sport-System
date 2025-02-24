package com.backend.golvia.app.services.activities;

import com.backend.golvia.app.dtos.activity_log.StatsDto;
import com.backend.golvia.app.entities.ActivityStats;
import com.backend.golvia.app.repositories.activities.ActivityStatsRepository;
import com.sun.jdi.request.DuplicateRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ActivityStatsService {

    private final ActivityStatsRepository activityStatsRepository;

    @Autowired
    public ActivityStatsService(ActivityStatsRepository activityStatsRepository) {
        this.activityStatsRepository = activityStatsRepository;
    }

    public ActivityStats createActivityStats(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or blank");
        }

        if (activityStatsRepository.findByUserEmail(email).isPresent()){
            throw new DuplicateRequestException("Activity Stat Entry exists.");
        }

        ActivityStats stats = new ActivityStats();
        stats.setUserEmail(email);
        stats.setImpressions(0);
        stats.setPostImpressions(0);
        stats.setProfileViews(0);

        return activityStatsRepository.save(stats);
    }

    public Optional<ActivityStats> getAllActivityStats(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or blank");
        }

        return activityStatsRepository.findByUserEmail(email);
    }

    public Optional<ActivityStats> getActivityStatsById(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or blank");
        }

        return activityStatsRepository.findByUserEmail(email);
    }

    public ActivityStats updateActivityStats(String email, StatsDto updatedActivityStats) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or blank");
        }

        return activityStatsRepository.findByUserEmail(email).map(existingActivityStats -> {
            existingActivityStats.setProfileViews(updatedActivityStats.getProfileViews());
            existingActivityStats.setPostImpressions(updatedActivityStats.getPostImpressions());
            existingActivityStats.setImpressions(updatedActivityStats.getImpressions());

            return activityStatsRepository.save(existingActivityStats);
        }).orElseThrow(() -> new RuntimeException("ActivityStats not found for email: " + email));
    }

    public void deleteActivityStats(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or blank");
        }

        if (activityStatsRepository.existsByUserEmail(email)) {
            activityStatsRepository.deleteByUserEmail(email);
        } else {
            throw new RuntimeException("ActivityStats not found for email: " + email);
        }
    }

    public void registerPostImpressions(String email) {
        ActivityStats stats = createOrFetchActivityStats(email);
        stats.setPostImpressions(stats.getPostImpressions() + 1);
        updateActivityStats(email, stats.toStatsDto());
    }

    public void registerProfileViews(String email) {
        ActivityStats stats = createOrFetchActivityStats(email);
        stats.setProfileViews(stats.getProfileViews() + 1);
        updateActivityStats(email, stats.toStatsDto());
    }

    private ActivityStats createOrFetchActivityStats(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or blank");
        }

        return activityStatsRepository.findByUserEmail(email).orElseGet(() -> {
            ActivityStats newStats = new ActivityStats();
            newStats.setUserEmail(email);
            newStats.setImpressions(0);
            newStats.setPostImpressions(0);
            newStats.setProfileViews(0);

            return activityStatsRepository.save(newStats);
        });
    }

}