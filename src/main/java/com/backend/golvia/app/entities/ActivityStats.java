package com.backend.golvia.app.entities;


import com.backend.golvia.app.dtos.activity_log.StatsDto;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "activity_stats")
public class ActivityStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_email")
    private String userEmail;

    @Column(name = "profile_views")
    private int profileViews;

    @Column(name = "post_impression")
    private int postImpressions;

    @Column(name = "impressions")
    private int impressions;

    public StatsDto toStatsDto(){
        StatsDto activityStats = new StatsDto();

        activityStats.setProfileViews(this.getProfileViews());
        activityStats.setPostImpressions(this.getPostImpressions());
        activityStats.setImpressions(this.getImpressions());

        return activityStats;
    }
}
