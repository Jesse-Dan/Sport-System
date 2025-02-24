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
public class OtpRequest {

    @Schema(description = "User's email", example = "support@gol-via.com")
    private String email;

    @Schema(description = "otp", example = "12345")
    private String otp;
}
