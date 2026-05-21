package com.conference.cms.controller;

import com.conference.cms.entity.Assignment;
import com.conference.cms.entity.Paper;
import com.conference.cms.entity.User;
import com.conference.cms.repository.AssignmentRepository;
import com.conference.cms.service.PaperService;
import com.conference.cms.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/assignments")
@RequiredArgsConstructor
public class AssignmentController {

    private final AssignmentRepository assignmentRepository;
    private final PaperService paperService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<?> assign(@RequestBody Map<String, Object> body) {
        Long paperId = Long.valueOf(body.get("paperId").toString());
        Long reviewerId = Long.valueOf(body.get("reviewerId").toString());

        // assignedById опционален
        Long assignedById = body.get("assignedById") != null
                ? Long.valueOf(body.get("assignedById").toString())
                : reviewerId;

        Paper paper = paperService.findById(paperId)
                .orElseThrow(() -> new RuntimeException("Статья не найдена"));
        User reviewer = userService.findById(reviewerId)
                .orElseThrow(() -> new RuntimeException("Рецензент не найден"));
        User assignedBy = userService.findById(assignedById)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        Assignment assignment = new Assignment();
        assignment.setPaper(paper);
        assignment.setReviewer(reviewer);
        assignment.setAssignedBy(assignedBy);
        assignmentRepository.save(assignment);

        paperService.changeStatus(paper, "under_review");

        return ResponseEntity.ok(Map.of("message", "Рецензент назначен"));
    }
    @GetMapping("/paper/{paperId}")
    public List<Assignment> getByPaper(@PathVariable Long paperId) {
        return assignmentRepository.findByPaperId(paperId);
    }
    @GetMapping("/reviewer/{reviewerId}")
    public ResponseEntity<?> getByReviewer(@PathVariable Long reviewerId) {
        List<Assignment> assignments = assignmentRepository.findByReviewerId(reviewerId);
        return ResponseEntity.ok(assignments);
    }
}