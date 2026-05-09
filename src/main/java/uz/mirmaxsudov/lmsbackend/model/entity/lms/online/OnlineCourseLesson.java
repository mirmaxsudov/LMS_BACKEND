package uz.mirmaxsudov.lmsbackend.model.entity.lms.online;

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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.mirmaxsudov.lmsbackend.model.entity.base.BaseEntity;
import uz.mirmaxsudov.lmsbackend.model.entity.content.Attachment;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.online.OnlineCourseContentStatus;

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
        name = "online_course_lessons",
        uniqueConstraints = @UniqueConstraint(columnNames = {"module_id", "order_index"})
)
public class OnlineCourseLesson extends BaseEntity {
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id", nullable = false)
    private OnlineCourseModule module;

    @NotBlank
    @Size(max = 255)
    @Column(nullable = false)
    private String title;

    @Size(max = 3000)
    private String description;

    @Size(max = 20000)
    @Column(columnDefinition = "TEXT")
    private String content;

    @NotNull
    @PositiveOrZero
    @Column(name = "order_index", nullable = false)
    private Integer orderIndex;

    @Column(name = "duration_in_minutes")
    private Integer durationInMinutes;

    @Column(name = "free_preview", nullable = false)
    private boolean freePreview = false;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OnlineCourseContentStatus status;

    @Column(name = "available_from")
    private LocalDateTime availableFrom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_attachment_id")
    private Attachment videoAttachment;

    @Builder.Default
    @OrderBy("orderIndex ASC")
    @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OnlineCourseLessonMaterial> materials = new ArrayList<>();
}
