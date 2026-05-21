package com.conference.cms.repository;

import com.conference.cms.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByPaperId(Long paperId);
    List<Review> findByReviewerId(Long reviewerId);
}