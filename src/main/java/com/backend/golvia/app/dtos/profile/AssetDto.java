package com.backend.golvia.app.dtos.profile;

import com.backend.golvia.app.entities.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssetDto {
    private String profilePictureUrl;
    private String profileReelUrl;
    private String coverPhotoUrl;
}