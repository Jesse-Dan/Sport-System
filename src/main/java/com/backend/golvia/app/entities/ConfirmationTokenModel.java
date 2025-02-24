package com.backend.golvia.app.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class ConfirmationTokenModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String token;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @ManyToOne
    @JoinColumn(nullable = false, name = "user_id")
    @JsonManagedReference
    private User user;
    /*
    It means that many confirmation tokens can be associated with a single user.For example, a user might request
    multiple confirmation emails, each generating a new confirmation token. This setup ensures that every token can
    be traced back to the user who owns it, which is the foreign key with column name user_id
     */


    public ConfirmationTokenModel(User user){
        this.token = token;
        this.createdAt = LocalDateTime.now();
        this.expiresAt = LocalDateTime.now().plusHours(24); // token valid for 24 hours
        this.user = user;
    }

}

