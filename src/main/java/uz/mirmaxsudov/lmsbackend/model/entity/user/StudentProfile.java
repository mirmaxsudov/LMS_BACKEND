package uz.mirmaxsudov.lmsbackend.model.entity.user;

import jakarta.persistence.*;
import lombok.*;
import uz.mirmaxsudov.lmsbackend.model.entity.auth.User;
import uz.mirmaxsudov.lmsbackend.model.entity.base.BaseEntity;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.Group;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.StudentStatus;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentProfile extends BaseEntity {
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String studentId;

    @ManyToOne(fetch = FetchType.LAZY)
    private Group group;

    @Enumerated(EnumType.STRING)
    private StudentStatus status;
}