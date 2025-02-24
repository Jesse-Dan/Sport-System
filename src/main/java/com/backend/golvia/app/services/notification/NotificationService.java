package com.backend.golvia.app.services.notification;

import com.backend.golvia.app.entities.Setting;
import com.backend.golvia.app.entities.User;
import com.backend.golvia.app.models.response.ApiResponse;
import com.backend.golvia.app.repositories.auth.SettingsRepository;
import com.backend.golvia.app.repositories.auth.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Service
public class NotificationService {

    private final UserRepository userRepository;
    private final SettingsRepository SettingRepository;


    @Transactional
    public ApiResponse<Void> toggleCommentNotification(String email) {
        // Validate the user exists
        User user = userRepository.findByEmail(email);

        // Fetch or create Setting for this user
        Setting Setting = SettingRepository.findByEmail(email)
                .orElseGet(() -> {
                    Setting newSettings = new Setting();
                    newSettings.setEmail(user.getEmail());
                    newSettings.setCommentNotificationEnabled(false); // Default value
                    return newSettings;
                });

        // Toggle the comment notification setting
        boolean currentStatus = Setting.isCommentNotificationEnabled();
        boolean newStatus = !currentStatus;

        Setting.setCommentNotificationEnabled(newStatus);

        // Save the updated settings
        SettingRepository.save(Setting);

        // Return an appropriate response
        String message = newStatus
                ? "Comment notification successfully enabled."
                : "Comment notification successfully disabled.";

        return new ApiResponse<>(200, message, null);
    }
    @Transactional
    public ApiResponse<Void> toggleConnectionNotification(String email) {
        // Validate the user exists
        User user = userRepository.findByEmail(email);

        // Fetch or create Setting for this user
        Setting Setting = SettingRepository.findByEmail(email)
                .orElseGet(() -> {
                    Setting newSettings = new Setting();
                    newSettings.setEmail(user.getEmail());
                    newSettings.setConnectionNotificationEnabled(true); // Default value
                    return newSettings;
                });

        // Toggle the connection notification setting
        boolean currentStatus = Setting.isConnectionNotificationEnabled();
        boolean newStatus = !currentStatus;

        Setting.setConnectionNotificationEnabled(newStatus);

        // Save the updated settings
        SettingRepository.save(Setting);

        // Return an appropriate response
        String message = newStatus
                ? "Connection notification successfully enabled."
                : "Connection notification successfully disabled.";

        return new ApiResponse<>(200, message, null);
    }

    @Transactional
    public ApiResponse<Void> toggleNetworkNotification(String email) {
        // Validate the user exists
        User user = userRepository.findByEmail(email);

        // Fetch or create Setting for this user
        Setting Setting = SettingRepository.findByEmail(email)
                .orElseGet(() -> {
                    Setting newSettings = new Setting();
                    newSettings.setEmail(user.getEmail());
                    newSettings.setNetworkNotificationEnabled(true); // Default value
                    return newSettings;
                });

        // Toggle the network notification setting
        boolean currentStatus = Setting.isNetworkNotificationEnabled();
        boolean newStatus = !currentStatus;

        Setting.setNetworkNotificationEnabled(newStatus);

        // Save the updated settings
        SettingRepository.save(Setting);

        // Return an appropriate response
        String message = newStatus
                ? "Network notification successfully enabled."
                : "Network notification successfully disabled.";

        return new ApiResponse<>(200, message, null);
    }

    @Transactional
    public ApiResponse<Void> toggleTagsNotification(String email) {
        // Validate the user exists
        User user = userRepository.findByEmail(email);

        // Fetch or create Setting for this user
        Setting Setting = SettingRepository.findByEmail(email)
                .orElseGet(() -> {
                    Setting newSettings = new Setting();
                    newSettings.setEmail(user.getEmail());
                    newSettings.setTagsNotificationEnabled(true); // Default value
                    return newSettings;
                });

        // Toggle the tags notification setting
        boolean currentStatus = Setting.isTagsNotificationEnabled();
        boolean newStatus = !currentStatus;

        Setting.setTagsNotificationEnabled(newStatus);

        // Save the updated settings
        SettingRepository.save(Setting);

        // Return an appropriate response
        String message = newStatus
                ? "Tags notification successfully enabled."
                : "Tags notification successfully disabled.";

        return new ApiResponse<>(200, message, null);
    }


}