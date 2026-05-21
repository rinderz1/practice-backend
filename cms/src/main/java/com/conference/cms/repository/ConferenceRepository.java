package com.conference.cms.repository;

import com.conference.cms.entity.Conference;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ConferenceRepository extends JpaRepository<Conference, Long> {
    List<Conference> findByStatus(String status);
}