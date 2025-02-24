package com.backend.golvia.app.dtos.interactions;

import lombok.Data;

@Data
public class LeaderboardData {
    private UserData user;
    private int creatives;
    private int likes;

    @Data
    public static class UserData {
        private int connections;
        private String firstName;
        private String lastName;
        private String email;
        private String profileImageUrl;
    }
}
