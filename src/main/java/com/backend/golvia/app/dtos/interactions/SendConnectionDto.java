package com.backend.golvia.app.dtos.interactions;

import lombok.Data;

import java.util.Optional;

@Data
public class SendConnectionDto {
    private String email;
    private String message = "Let's Connect";
}
