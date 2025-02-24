package com.backend.golvia.app.dtos.interactions;

import com.backend.golvia.app.enums.InteractionStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ConnectionDTO {
    private Long id;
    private InteractionStatus status;
    private String fromEmail;
    private String toEmail;
    private String reason;
    private LocalDateTime createdAt;
    private LocalDateTime acceptedAt;
}
