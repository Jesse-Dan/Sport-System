package com.backend.golvia.app.services.settings;


import com.backend.golvia.app.dtos.settings.UpdateSettingsDto;
import com.backend.golvia.app.entities.Setting;
import com.backend.golvia.app.entities.User;
import com.backend.golvia.app.repositories.auth.SettingsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class SettingsService {

    private final SettingsRepository settingsRepository;

    public SettingsService(SettingsRepository settingsRepository) {
        this.settingsRepository = settingsRepository;
    }

    public Setting initUserSetting(User user) throws Exception {
        Setting setting = checkSettings(user.getEmail());
        if(setting == null){
            Setting newsetting = new Setting();
            newsetting.setEmail(user.getEmail());
            try{
                return settingsRepository.save(newsetting);
            }catch (Exception ex){
                log.error(ex.getMessage());
            }
        }

     return setting;
    }

    public Setting updateUserSetting(User user, UpdateSettingsDto dto) throws Exception {
        Setting setting = checkSettings(user.getEmail());
        if(setting == null){
            setting = initUserSetting(user);
        }

        setting.setHas2FA(dto.isHas2FA());

        try{
            return settingsRepository.save(setting);
        }catch (Exception ex){
            log.error(ex.getMessage());
        }

        return setting;
    }



    public Setting getUserSetting(User user) throws Exception {
        Setting setting = checkSettings(user.getEmail());
        if(setting == null){
            initUserSetting(user);
            return settingsRepository.getSettingByEmail(user.getEmail());
        }
        return setting;
    }

    private Setting checkSettings(String email) throws Exception {
        try {
            return settingsRepository.getSettingByEmail(email);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new Exception("");
        }
    }

}
