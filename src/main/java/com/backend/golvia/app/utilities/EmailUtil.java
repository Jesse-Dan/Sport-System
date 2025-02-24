package com.backend.golvia.app.utilities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.backend.golvia.app.configs.UrlConfig;

@Component
public class EmailUtil {
    private final UrlConfig urlConfig;

    @Autowired
    public EmailUtil(UrlConfig urlConfig) {
        this.urlConfig = urlConfig;
    }


    public String getResetPasswordUrl(String token) {
        return urlConfig.getResetPasswordUrl() + token;
    }
}