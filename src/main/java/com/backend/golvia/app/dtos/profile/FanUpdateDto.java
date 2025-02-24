package com.backend.golvia.app.dtos.profile;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class FanUpdateDto {
    private String username;
    private String country;
    private String city;
    private String email;
    private List<String> favoriteSports;
    private List<String> favoriteAthletes;
    private List<String> notificationPreferences;
    private List<String> interactions;
    private List<String> purchasedItems;
    private AssetDto asset;
}
