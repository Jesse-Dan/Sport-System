package com.backend.golvia.app.entities;

import com.backend.golvia.app.enums.InteractionStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "connections")
public class Connection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "connection_status")
    private InteractionStatus status;

    @Column(name = "from_email")
    private String fromEmail;

    @Column(name = "to_email")
    private String toEmail;

    @Column(name = "connection_message")
    private String reason;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "accepted_at")
    private LocalDateTime acceptedAt;
}
