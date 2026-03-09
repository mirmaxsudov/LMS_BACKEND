package uz.mirmaxsudov.lmsbackend.model.entity.lms;

import jakarta.persistence.*;
import lombok.*;
import uz.mirmaxsudov.lmsbackend.model.entity.base.BaseEntity;
import uz.mirmaxsudov.lmsbackend.model.entity.user.StudentProfile;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Submission extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    private Assignment assignment;

    @ManyToOne(fetch = FetchType.LAZY)
    private StudentProfile student;

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime submissionDate;
    private double grade;
    private String feedback;
}
