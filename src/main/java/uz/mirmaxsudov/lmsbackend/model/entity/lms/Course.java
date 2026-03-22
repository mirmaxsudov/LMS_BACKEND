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
@Table(name = "courses", indexes = {
        @Index(columnList = "code", unique = true),
        @Index(columnList = "teacher_id")
})
public class Course extends BaseEntity {
    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private boolean isActive = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    private User teacher;

    @OneToMany(mappedBy = "course", fetch = FetchType.LAZY)
    private Set<LmsGroup> groups = new HashSet<>();
}
