package com.backend.golvia.app.dtos;

import com.backend.golvia.app.models.UserInfo;
import lombok.Data;

@Data
public class GoogleUserDtoRes {
    private String token;
    private UserInfo<?> userInfo;
}
