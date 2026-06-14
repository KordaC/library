package com.example.library.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({JwtProperties.class, QrProperties.class, PublicUrlProperties.class, FirebaseProperties.class})
public class AppConfig {
}
