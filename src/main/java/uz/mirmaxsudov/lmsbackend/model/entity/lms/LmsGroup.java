package uz.mirmaxsudov.lmsbackend.model.entity.lms;

import jakarta.persistence.*;
import lombok.*;
import uz.mirmaxsudov.lmsbackend.model.entity.auth.User;
import uz.mirmaxsudov.lmsbackend.model.entity.base.BaseEntity;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "lms_groups", indexes = {
        @Index(columnList = "course_id"),
        @Index(columnList = "teacher_id")
})
public class LmsGroup extends BaseEntity {
    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    private User teacher;

    private Integer capacity;

    @Column(nullable = false)
    private boolean isActive = true;

    @OneToMany(mappedBy = "group", fetch = FetchType.LAZY)
    private Set<Enrollment> enrollments = new HashSet<>();
}
