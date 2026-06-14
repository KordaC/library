package com.example.library.config;

import com.example.library.repository.UserAccountRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Profile({"dev", "postgres", "file"})
public class DevPasswordInitializer implements ApplicationRunner {

    private static final UUID DEMO_USER_ID = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");

    private final UserAccountRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DevPasswordInitializer(UserAccountRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        userRepository.findById(DEMO_USER_ID).ifPresent(user -> {
            user.setPasswordHash(passwordEncoder.encode("Demo1234"));
            userRepository.save(user);
        });
    }
}
