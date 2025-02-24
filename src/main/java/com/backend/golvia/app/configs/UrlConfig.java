package com.backend.golvia.app.configs;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "url")
@RequiredArgsConstructor
public class UrlConfig {

    private String resetPasswordUrl;
}
