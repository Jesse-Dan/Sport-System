package com.backend.golvia.app.repositories.auth;

import com.backend.golvia.app.entities.ConfirmationTokenModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationTokenModel, Long> {

    Optional<ConfirmationTokenModel> findByToken(String token);
    void deleteByUserId(Long userId);
}