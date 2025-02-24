package com.backend.golvia.app.models.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginRequest {

    @Schema(description = "User's email", example = "support@gol-via.com")
    private String email;

    @Schema(description = "User's password", example = "Golvia@1")
    private String password;
}
