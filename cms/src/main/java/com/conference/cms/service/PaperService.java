package com.conference.cms.service;

import com.conference.cms.entity.Paper;
import com.conference.cms.repository.PaperRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaperService {

    private final PaperRepository paperRepository;

    private static final Map<String, List<String>> TRANSITIONS = Map.of(
            "draft",        List.of("submitted", "withdrawn"),
            "submitted",    List.of("under_review", "withdrawn"),
            "under_review", List.of("reviewed", "accepted", "rejected", "revision"),
            "reviewed",     List.of("accepted", "rejected", "revision"),
            "revision",     List.of("submitted", "withdrawn")
    );
    public Paper submit(Paper paper) {
        paper.setStatus("submitted");
        return paperRepository.save(paper);
    }

    public Optional<Paper> findById(Long id) {
        return paperRepository.findById(id);
    }

    public List<Paper> findByConference(Long conferenceId) {
        return paperRepository.findByConferenceId(conferenceId);
    }

    public List<Paper> findByAuthor(Long userId) {
        return paperRepository.findBySubmittingAuthorId(userId);
    }

    public Paper save(Paper paper) {
        return paperRepository.save(paper);
    }

    public List<Paper> findAll() {
        return paperRepository.findAll();
    }

    public boolean changeStatus(Paper paper, String newStatus) {
        List<String> allowed = TRANSITIONS.getOrDefault(paper.getStatus(), List.of());
        if (!allowed.contains(newStatus)) return false;
        paper.setStatus(newStatus);
        paperRepository.save(paper);
        return true;
    }
}