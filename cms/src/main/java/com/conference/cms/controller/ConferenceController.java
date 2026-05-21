package com.conference.cms.controller;

import com.conference.cms.entity.Conference;
import com.conference.cms.entity.ConferenceChair;
import com.conference.cms.entity.User;
import com.conference.cms.repository.ConferenceChairRepository;
import com.conference.cms.repository.ConferenceRepository;
import com.conference.cms.service.ConferenceService;
import com.conference.cms.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/conferences")
@RequiredArgsConstructor
public class ConferenceController {

    private final ConferenceService conferenceService;
    private final UserService userService;
    private final ConferenceRepository conferenceRepository;
    private final ConferenceChairRepository conferenceChairRepository;

    @GetMapping
    public ResponseEntity<?> getAll() {
        List<Conference> conferences = conferenceService.findAll();
        return ResponseEntity.ok(conferences);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return conferenceService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> create(
            @RequestBody Map<String, Object> body,
            @RequestParam Long userId) {
        Optional<User> user = userService.findById(userId);
        if (user.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Пользователь не найден"));
        }
        Conference conf = new Conference();
        conf.setTitle(body.get("title").toString());
        if (body.get("description") != null)
            conf.setDescription(body.get("description").toString());
        if (body.get("venue") != null)
            conf.setVenue(body.get("venue").toString());
        if (body.get("status") != null)
            conf.setStatus(body.get("status").toString());
        if (body.get("startDate") != null)
            conf.setStartDate(java.time.LocalDate.parse(body.get("startDate").toString()));
        if (body.get("endDate") != null)
            conf.setEndDate(java.time.LocalDate.parse(body.get("endDate").toString()));
        if (body.get("submissionDeadline") != null)
            conf.setSubmissionDeadline(java.time.LocalDate.parse(body.get("submissionDeadline").toString()));
        conf.setCreatedBy(user.get());
        Conference saved = conferenceService.save(conf);

        // Назначаем председателей если переданы
        if (body.get("conferenceChairs") != null) {
            assignChairs(saved, body.get("conferenceChairs"));
        }

        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body) {
        return conferenceService.findById(id).map(conf -> {
            if (body.get("title") != null)
                conf.setTitle(body.get("title").toString());
            if (body.get("description") != null)
                conf.setDescription(body.get("description").toString());
            if (body.get("venue") != null)
                conf.setVenue(body.get("venue").toString());
            if (body.get("status") != null)
                conf.setStatus(body.get("status").toString());
            if (body.get("startDate") != null)
                conf.setStartDate(java.time.LocalDate.parse(body.get("startDate").toString()));
            if (body.get("endDate") != null)
                conf.setEndDate(java.time.LocalDate.parse(body.get("endDate").toString()));
            if (body.get("submissionDeadline") != null)
                conf.setSubmissionDeadline(java.time.LocalDate.parse(body.get("submissionDeadline").toString()));

            Conference saved = conferenceService.save(conf);

            // Обновляем председателей
            if (body.get("conferenceChairs") != null) {
                conferenceChairRepository.deleteAll(
                        conferenceChairRepository.findByConferenceId(id)
                );
                assignChairs(saved, body.get("conferenceChairs"));
                // Обновляем роль пользователей
            }

            return ResponseEntity.ok(saved);
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        return conferenceService.findById(id).map(conf -> {
            conferenceRepository.delete(conf);
            return ResponseEntity.ok(Map.of("message", "Конференция удалена"));
        }).orElse(ResponseEntity.notFound().build());
    }

    @SuppressWarnings("unchecked")
    private void assignChairs(Conference conf, Object chairsData) {
        try {
            List<Map<String, Object>> chairs = (List<Map<String, Object>>) chairsData;
            for (Map<String, Object> chairData : chairs) {
                Long userId = Long.valueOf(chairData.get("id").toString());
                userService.findById(userId).ifPresent(user -> {
                    ConferenceChair chair = new ConferenceChair();
                    chair.setConference(conf);
                    chair.setUser(user);
                    conferenceChairRepository.save(chair);
                    user.setSystemRole("chair");
                    userService.save(user);
                });
            }
        } catch (Exception e) {
            // ignore
        }
    }
}