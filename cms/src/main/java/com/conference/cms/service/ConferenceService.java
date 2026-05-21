package com.conference.cms.service;

import com.conference.cms.entity.Conference;
import com.conference.cms.entity.User;
import com.conference.cms.repository.ConferenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ConferenceService {

    private final ConferenceRepository conferenceRepository;

    public List<Conference> findAll() {
        return conferenceRepository.findAll();
    }

    public Optional<Conference> findById(Long id) {
        return conferenceRepository.findById(id);
    }

    public Conference create(Conference conference, User createdBy) {
        conference.setCreatedBy(createdBy);
        return conferenceRepository.save(conference);
    }

    public Conference save(Conference conference) {
        return conferenceRepository.save(conference);
    }

    public List<Conference> findByStatus(String status) {
        return conferenceRepository.findByStatus(status);
    }
}