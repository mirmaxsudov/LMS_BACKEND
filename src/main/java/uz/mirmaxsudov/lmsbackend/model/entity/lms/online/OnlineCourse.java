package uz.mirmaxsudov.lmsbackend.model.entity.lms.online;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import uz.mirmaxsudov.lmsbackend.model.entity.auth.User;
import uz.mirmaxsudov.lmsbackend.model.entity.base.BaseEntity;
import uz.mirmaxsudov.lmsbackend.model.entity.content.Attachment;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.CourseLevel;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.online.OnlineCourseStatus;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.online.OnlineCourseUnlockStrategy;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "online_courses",
        uniqueConstraints = @UniqueConstraint(columnNames = "slug"),
        indexes = {
                @Index(columnList = "slug", unique = true)
        }
)
public class OnlineCourse extends BaseEntity {
    @NotBlank
    @Size(max = 255)
    @Column(nullable = false)
    private String title;

    @NotBlank
    @Size(max = 255)
    @Column(nullable = false)
    private String slug;

    @Size(max = 500)
    @Column(name = "short_description")
    private String shortDescription;

    @Size(max = 10000)
    @Column(columnDefinition = "TEXT")
    private String description;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CourseLevel level;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OnlineCourseStatus status;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "unlock_strategy", nullable = false)
    private OnlineCourseUnlockStrategy unlockStrategy;

    @Column(name = "estimated_duration_in_minutes")
    private Integer estimatedDurationInMinutes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "thumbnail_id")
    private Attachment thumbnail;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id", nullable = false)
    private User createdBy;

    @Builder.Default
    @OrderBy("orderIndex ASC")
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OnlineCourseModule> modules = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OnlineCourseEnrollment> enrollments = new ArrayList<>();
}
