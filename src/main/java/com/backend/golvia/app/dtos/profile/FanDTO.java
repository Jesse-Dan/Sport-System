package com.backend.golvia.app.dtos.profile;

import java.util.List;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Data
public class FanDTO {
    private Long id;
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