package com.backend.golvia.app.repositories.auth;

import com.backend.golvia.app.entities.Setting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface SettingsRepository extends JpaRepository<Setting, Long> {
    Setting getSettingByEmail(String email);

    Optional<Setting> findByEmail(String email);

    void deleteByEmail(String email);
}
