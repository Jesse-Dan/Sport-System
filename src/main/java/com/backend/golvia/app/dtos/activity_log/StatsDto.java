package com.backend.golvia.app.dtos.activity_log;

import com.backend.golvia.app.entities.ActivityStats;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class StatsDto {

    @NotBlank()
    private int profileViews;

    @NotBlank()
    private int postImpressions;

    @NotBlank()
    private int impressions;

    public ActivityStats toActivityStats(){
        ActivityStats activityStats = new ActivityStats();

        activityStats.setProfileViews(this.getProfileViews());
        activityStats.setPostImpressions(this.getPostImpressions());
        activityStats.setImpressions(this.getImpressions());

        return activityStats;
    }




}
