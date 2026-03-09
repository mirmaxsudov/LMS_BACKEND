package uz.mirmaxsudov.lmsbackend.model.entity.lms;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.*;
import uz.mirmaxsudov.lmsbackend.model.entity.base.BaseEntity;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Department extends BaseEntity {
    private String name;
    private String description;
    @ManyToOne(fetch = FetchType.EAGER)
    private Faculty faculty;
}
