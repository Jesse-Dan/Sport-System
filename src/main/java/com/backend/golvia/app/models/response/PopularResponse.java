package com.backend.golvia.app.models.response;


import lombok.*;

@Data
@RequiredArgsConstructor
@Builder
public class PopularResponse {
    private String clubName;
    private String imageUrl;
    private int followers; // Using the count of social links as a proxy for followers


    public PopularResponse(String clubName, String imageUrl, int followers) {
        this.clubName = clubName;
        this.imageUrl = imageUrl;
        this.followers = followers;
    }

    public PopularResponse(String clubName, String teamLogoUrl, long followersCount, long postsCount) {

    }
}
