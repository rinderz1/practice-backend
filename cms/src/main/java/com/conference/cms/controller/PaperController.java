package com.conference.cms.controller;

import com.conference.cms.entity.Conference;
import com.conference.cms.entity.Paper;
import com.conference.cms.entity.User;
import com.conference.cms.service.ConferenceService;
import com.conference.cms.service.PaperService;
import com.conference.cms.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
@RestController
@RequestMapping("/api/papers")
@RequiredArgsConstructor

public class PaperController {

    private final PaperService paperService;
    private final UserService userService;
    private final ConferenceService conferenceService;

    @GetMapping("/conference/{conferenceId}")
    public List<Paper> getByConference(@PathVariable Long conferenceId) {
        return paperService.findByConference(conferenceId);
    }

    @GetMapping("/author/{userId}")
    public List<Paper> getByAuthor(@PathVariable Long userId) {
        return paperService.findByAuthor(userId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return paperService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @GetMapping
    public List<Paper> getAll() {
        return paperService.findAll();
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Map<String, Object> body) {
        Long userId = Long.valueOf(body.get("userId").toString());
        Long confId = Long.valueOf(body.get("conferenceId").toString());

        Optional<User> user = userService.findById(userId);
        Optional<Conference> conf = conferenceService.findById(confId);

        if (user.isEmpty() || conf.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Пользователь или конференция не найдены"));
        }

        Paper paper = new Paper();
        paper.setTitle(body.get("title").toString());
        paper.setAbstractText(body.get("abstractText").toString());
        paper.setSubmittingAuthor(user.get());
        paper.setConference(conf.get());
        paper.setStatus("submitted");

        return ResponseEntity.ok(paperService.save(paper));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> changeStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        return paperService.findById(id).map(paper -> {
            boolean ok = paperService.changeStatus(paper, body.get("status"));
            if (!ok) return ResponseEntity.badRequest()
                    .body(Map.of("error", "Недопустимый переход статуса"));
            return ResponseEntity.ok(Map.of("status", paper.getStatus()));
        }).orElse(ResponseEntity.notFound().build());
    }
    @PostMapping("/{id}/upload")
    public ResponseEntity<?> uploadPdf(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) throws IOException {

        if (!Objects.requireNonNull(file.getContentType()).equals("application/pdf")) {
            return ResponseEntity.badRequest().body(Map.of("error", "Только PDF файлы"));
        }
        if (file.getSize() > 10 * 1024 * 1024) {
            return ResponseEntity.badRequest().body(Map.of("error", "Файл больше 10МБ"));
        }

        return paperService.findById(id).map(paper -> {
            try {
                String uploadDir = "uploads/papers/";
                File dir = new File(uploadDir);
                if (!dir.exists()) dir.mkdirs();

                String fileName = id + "_" + System.currentTimeMillis() + ".pdf";
                Path filePath = Paths.get(uploadDir + fileName);
                Files.write(filePath, file.getBytes());

                paper.setFilePath(uploadDir + fileName);
                paperService.save(paper);

                return ResponseEntity.ok(Map.of("filePath", uploadDir + fileName));
            } catch (IOException e) {
                return ResponseEntity.internalServerError()
                        .body(Map.of("error", "Ошибка сохранения файла"));
            }
        }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<?> downloadPdf(
            @PathVariable Long id,
            HttpServletResponse response) throws IOException {

        return paperService.findById(id).map(paper -> {
            if (paper.getFilePath() == null) {
                return ResponseEntity.notFound().<Void>build();
            }
            try {
                Path filePath = Paths.get(paper.getFilePath());
                byte[] fileBytes = Files.readAllBytes(filePath);

                return ResponseEntity.ok()
                        .header("Content-Type", "application/pdf")
                        .header("Content-Disposition",
                                "attachment; filename=\"paper-" + id + ".pdf\"")
                        .body(fileBytes);
            } catch (IOException e) {
                return ResponseEntity.internalServerError().<byte[]>build();
            }
        }).orElse(ResponseEntity.notFound().build());
    }
}