package uz.mirmaxsudov.lmsbackend.model.entity.lms;

import jakarta.persistence.*;
import lombok.*;
import uz.mirmaxsudov.lmsbackend.model.entity.base.BaseEntity;
import uz.mirmaxsudov.lmsbackend.model.entity.user.StudentProfile;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.EnrollmentStatus;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Enrollment extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    private StudentProfile student;

    @ManyToOne(fetch = FetchType.LAZY)
    private Course course;

    private LocalDateTime enrollmentDate;

    @Enumerated(EnumType.STRING)
    private EnrollmentStatus status;
}
