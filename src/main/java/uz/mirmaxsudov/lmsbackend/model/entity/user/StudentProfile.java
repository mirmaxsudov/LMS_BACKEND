package uz.mirmaxsudov.lmsbackend.model.entity.user;

import jakarta.persistence.*;
import lombok.*;
import uz.mirmaxsudov.lmsbackend.model.entity.auth.User;
import uz.mirmaxsudov.lmsbackend.model.entity.base.BaseEntity;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.StudentStatus;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

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

    private UUID studentId;

    @Enumerated(EnumType.STRING)
    private StudentStatus status;

    @ManyToMany(mappedBy = "students", fetch = FetchType.LAZY)
    private Set<ParentProfile> parents = new HashSet<>();

}
