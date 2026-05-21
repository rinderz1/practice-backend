package com.conference.cms.repository;

import com.conference.cms.entity.ConferenceChair;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ConferenceChairRepository extends JpaRepository<ConferenceChair, Long> {
    List<ConferenceChair> findByConferenceId(Long conferenceId);
    List<ConferenceChair> findByUserId(Long userId);
}