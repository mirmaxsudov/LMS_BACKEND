package uz.mirmaxsudov.lmsbackend.model.entity.lms.online;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.mirmaxsudov.lmsbackend.model.entity.base.BaseEntity;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.online.OnlineCourseProgressStatus;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "online_course_lesson_progresses",
        uniqueConstraints = @UniqueConstraint(columnNames = {"enrollment_id", "lesson_id"})
)
public class OnlineCourseLessonProgress extends BaseEntity {
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enrollment_id", nullable = false)
    private OnlineCourseEnrollment enrollment;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id", nullable = false)
    private OnlineCourseLesson lesson;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OnlineCourseProgressStatus status;

    @PositiveOrZero
    @Column(name = "last_position_in_seconds")
    private Integer lastPositionInSeconds;

    @Column(name = "opened_at")
    private LocalDateTime openedAt;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;
}
