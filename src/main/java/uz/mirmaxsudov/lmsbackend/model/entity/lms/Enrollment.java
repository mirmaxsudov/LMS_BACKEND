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
@Table(name = "enrollments", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"group_id", "student_profile_id"})
}, indexes = {
        @Index(columnList = "group_id"),
        @Index(columnList = "student_profile_id"),
        @Index(columnList = "status")
})
public class Enrollment extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private LmsGroup group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_profile_id", nullable = false)
    private StudentProfile studentProfile;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EnrollmentStatus status = EnrollmentStatus.ACTIVE;

    @Column(nullable = false)
    private LocalDateTime enrolledAt;

    @PrePersist
    public void prePersistEnrollment() {
        if (enrolledAt == null) {
            enrolledAt = LocalDateTime.now();
        }
    }
}
