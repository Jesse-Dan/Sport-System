package com.backend.golvia.app.dtos.interactions;

import com.backend.golvia.app.enums.InteractionStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FollowDTO {
    private Long id;
    private InteractionStatus status;
    private String fromEmail;
    private String toEmail;
    private LocalDateTime createdAt;
}