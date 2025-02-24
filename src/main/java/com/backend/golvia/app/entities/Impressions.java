package com.backend.golvia.app.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class Impressions {
    @Column(name = "creative_count", nullable = true)
    private String creativeImpressions = "0";

    @Column(name = "like_count", nullable = true)
    private String likeImpressions = "0";
}
