package com.conference.cms.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Data
@Entity
@Table(name = "conferences")
public class Conference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String venue;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate submissionDeadline;
    private LocalDate reviewDeadline;

    @Column(nullable = false)
    private String status = "upcoming";

    @ManyToOne
    @JoinColumn(name = "created_by")
    @JsonIgnoreProperties({"passwordHash", "bio", "photoPath"})
    private User createdBy;

    @OneToMany(mappedBy = "conference", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<ConferenceChair> conferenceChairs = new ArrayList<>();

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}