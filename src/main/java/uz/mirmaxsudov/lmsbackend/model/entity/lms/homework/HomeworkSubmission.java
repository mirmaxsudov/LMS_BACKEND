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
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.mirmaxsudov.lmsbackend.model.entity.base.BaseEntity;
import uz.mirmaxsudov.lmsbackend.model.entity.user.StudentProfile;
import uz.mirmaxsudov.lmsbackend.model.entity.user.TeacherProfile;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.homework.HomeworkSubmissionStatus;

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
@Table(
        name = "homework_submissions",
        uniqueConstraints = @UniqueConstraint(columnNames = {"homework_id", "student_profile_id", "attempt_number"})
)
public class HomeworkSubmission extends BaseEntity {
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "homework_id", nullable = false)
    private Homework homework;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_profile_id", nullable = false)
    private StudentProfile student;

    @NotNull
    @Positive
    @Column(name = "attempt_number", nullable = false)
    private Integer attemptNumber;

    @Size(max = 20000)
    @Column(name = "answer_text", columnDefinition = "TEXT")
    private String answerText;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HomeworkSubmissionStatus status;

    @NotNull
    @Column(name = "submitted_at", nullable = false)
    private LocalDateTime submittedAt;

    @Column(name = "late", nullable = false)
    private boolean late;

    @DecimalMin(value = "0.0")
    @Column(precision = 8, scale = 2)
    private BigDecimal grade;

    @Size(max = 5000)
    @Column(name = "teacher_feedback", columnDefinition = "TEXT")
    private String teacherFeedback;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "graded_by_id")
    private TeacherProfile gradedBy;

    @Column(name = "graded_at")
    private LocalDateTime gradedAt;

    @Builder.Default
    @OrderBy("orderIndex ASC")
    @OneToMany(mappedBy = "submission", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HomeworkSubmissionAttachment> attachments = new ArrayList<>();

    @AssertTrue(message = "Homework submission must contain text or at least one file")
    private boolean hasTextOrFile() {
        return (answerText != null && !answerText.isBlank()) || (attachments != null && !attachments.isEmpty());
    }

    @AssertTrue(message = "Grade metadata is incomplete")
    private boolean isGradeMetadataValid() {
        boolean hasGradeData = grade != null || gradedBy != null || gradedAt != null;
        return !hasGradeData || (grade != null && gradedBy != null && gradedAt != null);
    }
}
