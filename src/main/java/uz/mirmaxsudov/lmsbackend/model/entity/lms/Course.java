package uz.mirmaxsudov.lmsbackend.model.entity.lms;

import jakarta.persistence.*;
import lombok.*;
import uz.mirmaxsudov.lmsbackend.model.entity.auth.User;
import uz.mirmaxsudov.lmsbackend.model.entity.base.BaseEntity;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Course extends BaseEntity {
    @Column(nullable = false)
    private String name;
    @Column(nullable = false, length = 1000, columnDefinition = "TEXT")
    private String description;
    private boolean isActive;
    @ManyToOne
    @JoinColumn(name = "created_by_id")
    private User createdBy;
}