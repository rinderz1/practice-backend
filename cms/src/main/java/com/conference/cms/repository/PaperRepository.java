package com.conference.cms.repository;

import com.conference.cms.entity.Paper;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PaperRepository extends JpaRepository<Paper, Long> {
    List<Paper> findByConferenceId(Long conferenceId);
    List<Paper> findBySubmittingAuthorId(Long userId);
    List<Paper> findByConferenceIdAndStatus(Long conferenceId, String status);
}