package com.backend.golvia.app.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "cloudinary_response")
@Builder
public class CloudinaryResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String publicId;
    private String result;


    @Column(nullable = false)
    private LocalDateTime timestamp;
}
