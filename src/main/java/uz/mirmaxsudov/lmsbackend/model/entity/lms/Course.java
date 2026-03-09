package uz.mirmaxsudov.lmsbackend.model.entity.lms;

import jakarta.persistence.*;
import lombok.*;
import uz.mirmaxsudov.lmsbackend.model.entity.base.BaseEntity;
import uz.mirmaxsudov.lmsbackend.model.entity.user.TeacherProfile;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.CourseOfferingStatus;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Course extends BaseEntity {
    private String name;
    private String description;
    private String courseCode;
    private int credits;

    @ManyToOne(fetch = FetchType.EAGER)
    private Semester semester;

    @ManyToOne(fetch = FetchType.EAGER)
    private Department department;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "course_teachers",
            joinColumns = @JoinColumn(name = "course_id"),
            inverseJoinColumns = @JoinColumn(name = "teacher_id")
    )
    private Set<TeacherProfile> teachers = new HashSet<>();

    @Enumerated(EnumType.STRING)
    private CourseOfferingStatus status;
}
