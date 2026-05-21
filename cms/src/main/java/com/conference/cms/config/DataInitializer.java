package com.conference.cms.config;

import com.conference.cms.entity.User;
import com.conference.cms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {

        if (userRepository.findByEmail("newadmin@example.com").isEmpty()) {

            User admin = new User();

            admin.setFullName("Admin");
            admin.setEmail("newadmin@example.com");
            admin.setPasswordHash(passwordEncoder.encode("qwerty"));
            admin.setSystemRole("admin");
            admin.setIsActive(true);

            userRepository.save(admin);
        }

        if (userRepository.findByEmail("newchair@example.com").isEmpty()) {

            User chair = new User();

            chair.setFullName("Chair User");
            chair.setEmail("newchair@example.com");
            chair.setPasswordHash(passwordEncoder.encode("qwerty"));
            chair.setSystemRole("chair");
            chair.setIsActive(true);

            userRepository.save(chair);
        }
    }
}