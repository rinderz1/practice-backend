package com.conference.cms.controller;

import com.conference.cms.entity.Paper;
import com.conference.cms.entity.Review;
import com.conference.cms.entity.User;
import com.conference.cms.repository.ReviewRepository;
import com.conference.cms.service.PaperService;
import com.conference.cms.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewRepository reviewRepository;
    private final PaperService paperService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<?> submitReview(@RequestBody Map<String, Object> body) {
        Long paperId = Long.valueOf(body.get("paperId").toString());
        Long reviewerId = Long.valueOf(body.get("reviewerId").toString());

        Paper paper = paperService.findById(paperId)
                .orElseThrow(() -> new RuntimeException("Статья не найдена"));
        User reviewer = userService.findById(reviewerId)
                .orElseThrow(() -> new RuntimeException("Рецензент не найден"));

        Review review = new Review();
        review.setPaper(paper);
        review.setReviewer(reviewer);
        review.setOriginalityScore(Integer.valueOf(body.get("originalityScore").toString()));
        review.setTechnicalScore(Integer.valueOf(body.get("technicalScore").toString()));
        review.setClarityScore(Integer.valueOf(body.get("clarityScore").toString()));
        review.setRelevanceScore(Integer.valueOf(body.get("relevanceScore").toString()));
        review.setOverallScore(Integer.valueOf(body.get("overallScore").toString()));
        review.setComments(body.get("comments").toString());
        if (body.get("privateComments") != null)
            review.setPrivateComments(body.get("privateComments").toString());
        review.setIsSubmitted(true);

        reviewRepository.save(review);

        return ResponseEntity.ok(Map.of("message", "Рецензия отправлена"));
    }

    @GetMapping("/paper/{paperId}")
    public List<Review> getByPaper(@PathVariable Long paperId) {
        return reviewRepository.findByPaperId(paperId);
    }
}