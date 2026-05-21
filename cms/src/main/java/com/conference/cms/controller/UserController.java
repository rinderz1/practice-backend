package com.conference.cms.controller;

import com.conference.cms.entity.User;
import com.conference.cms.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor

public class UserController {

    private final UserService userService;

    @GetMapping
    public List<User> getAll() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return userService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        return userService.findById(id).map(user -> {
            user.setFullName(body.getOrDefault("fullName", user.getFullName()));
            user.setAffiliation(body.getOrDefault("affiliation", user.getAffiliation()));
            user.setCountry(body.getOrDefault("country", user.getCountry()));
            user.setOrcidId(body.getOrDefault("orcidId", user.getOrcidId()));
            user.setBio(body.getOrDefault("bio", user.getBio()));
            return ResponseEntity.ok(userService.save(user));
        }).orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/role")
    public ResponseEntity<?> updateRole(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        return userService.findById(id).map(user -> {
            user.setSystemRole(body.get("systemRole"));
            return ResponseEntity.ok(userService.save(user));
        }).orElse(ResponseEntity.notFound().build());
    }
}