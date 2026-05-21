package com.conference.cms.controller;

import com.conference.cms.entity.User;
import com.conference.cms.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor

public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> body) {
        try {
            User user = userService.register(
                    body.get("fullName"),
                    body.get("email"),
                    body.get("password")
            );
            return ResponseEntity.ok(Map.of(
                    "id", user.getId(),
                    "fullName", user.getFullName(),
                    "email", user.getEmail(),
                    "systemRole", user.getSystemRole()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        Optional<User> userOpt = userService.findByEmail(body.get("email"));
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Неверный email или пароль"));
        }
        User user = userOpt.get();
        if (!userService.checkPassword(user, body.get("password"))) {
            return ResponseEntity.badRequest().body(Map.of("error", "Неверный email или пароль"));
        }
        return ResponseEntity.ok(Map.of(
                "id", user.getId(),
                "fullName", user.getFullName(),
                "email", user.getEmail(),
                "systemRole", user.getSystemRole()
        ));
    }
}