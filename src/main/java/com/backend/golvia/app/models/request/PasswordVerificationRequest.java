package com.backend.golvia.app.models.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PasswordVerificationRequest {

    @NotBlank(message = "Current is required")
    private String currentPassword;

}
