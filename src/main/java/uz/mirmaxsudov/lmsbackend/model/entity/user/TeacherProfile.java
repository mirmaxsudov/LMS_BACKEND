package uz.mirmaxsudov.lmsbackend.model.entity.user;

import jakarta.persistence.*;
import lombok.*;
import uz.mirmaxsudov.lmsbackend.model.entity.auth.User;
import uz.mirmaxsudov.lmsbackend.model.entity.base.BaseEntity;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.Department;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.TeacherPosition;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeacherProfile extends BaseEntity {
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String employeeId;

    @Enumerated(EnumType.STRING)
    private TeacherPosition position;

    @ManyToOne(fetch = FetchType.LAZY)
    private Department department;
}