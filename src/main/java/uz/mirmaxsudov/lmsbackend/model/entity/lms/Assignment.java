package uz.mirmaxsudov.lmsbackend.model.entity.lms;

import jakarta.persistence.*;
import lombok.*;
import uz.mirmaxsudov.lmsbackend.model.entity.base.BaseEntity;
import uz.mirmaxsudov.lmsbackend.model.entity.user.TeacherProfile;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.AssignmentStatus;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Assignment extends BaseEntity {
    private String title;
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    private TeacherProfile teacher;

    private LocalDateTime dueDate;
    private double maxGrade;

    @Enumerated(EnumType.STRING)
    private AssignmentStatus status;
}
