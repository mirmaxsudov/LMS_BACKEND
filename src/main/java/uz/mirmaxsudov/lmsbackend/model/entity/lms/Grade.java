package uz.mirmaxsudov.lmsbackend.model.entity.lms;

import jakarta.persistence.*;
import lombok.*;
import uz.mirmaxsudov.lmsbackend.model.entity.base.BaseEntity;
import uz.mirmaxsudov.lmsbackend.model.entity.user.StudentProfile;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.GradeType;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Grade extends BaseEntity {
    private double value;
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    private StudentProfile student;

    @ManyToOne(fetch = FetchType.LAZY)
    private Course course;

    @Enumerated(EnumType.STRING)
    private GradeType type;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_id")
    private Submission submission;
}
