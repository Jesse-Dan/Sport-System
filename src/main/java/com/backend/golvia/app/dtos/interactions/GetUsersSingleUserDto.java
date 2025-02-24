package com.backend.golvia.app.dtos.interactions;

import com.backend.golvia.app.enums.ProfileType;
import lombok.Data;

import java.util.Map;

@Data
public class GetUsersSingleUserDto {
    private String email;
    private String firstName;
    private String lastName;
    private String country;
    private ProfileType profileType;
    private String sportType ;
    private String teamName ;
    private boolean active = false;
    private String profilePictureUrl;
    private String profileRealUrl;
    private Map<String, Object> metaData;
}
