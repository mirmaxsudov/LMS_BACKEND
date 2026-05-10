package uz.mirmaxsudov.lmsbackend.model.entity.lms.homework;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.mirmaxsudov.lmsbackend.model.entity.base.BaseEntity;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.Group;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.Lesson;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.LessonSession;
import uz.mirmaxsudov.lmsbackend.model.entity.user.TeacherProfile;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.homework.HomeworkStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "homeworks")
public class Homework extends BaseEntity {
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    private TeacherProfile teacher;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id")
    private Lesson lesson;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_session_id")
    private LessonSession lessonSession;

    @NotBlank
    @Size(max = 255)
    @Column(nullable = false)
    private String title;

    @Size(max = 10000)
    @Column(columnDefinition = "TEXT")
    private String description;

    @Size(max = 20000)
    @Column(columnDefinition = "TEXT")
    private String instructions;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HomeworkStatus status;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "allow_late_submission", nullable = false)
    private boolean allowLateSubmission;

    @NotNull
    @Positive
    @Column(name = "max_submission_attempts", nullable = false)
    private Integer maxSubmissionAttempts;

    @NotNull
    @PositiveOrZero
    @Column(name = "max_files_per_submission", nullable = false)
    private Integer maxFilesPerSubmission;

    @PositiveOrZero
    @Column(name = "max_text_length")
    private Integer maxTextLength;

    @NotNull
    @DecimalMin(value = "0.0")
    @Column(name = "max_score", nullable = false, precision = 8, scale = 2)
    private BigDecimal maxScore;

    @Builder.Default
    @OrderBy("orderIndex ASC")
    @OneToMany(mappedBy = "homework", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HomeworkAttachment> attachments = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "homework", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HomeworkAssignee> assignees = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "homework", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HomeworkSubmission> submissions = new ArrayList<>();

    @AssertTrue(message = "Homework expiration time must be after publish time")
    private boolean isExpirationTimeValid() {
        return publishedAt == null || expiresAt == null || expiresAt.isAfter(publishedAt);
    }
}
