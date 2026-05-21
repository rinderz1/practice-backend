package com.conference.cms.repository;

import com.conference.cms.entity.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    List<Assignment> findByPaperId(Long paperId);
    List<Assignment> findByReviewerId(Long reviewerId);
    Optional<Assignment> findByPaperIdAndReviewerId(Long paperId, Long reviewerId);
}