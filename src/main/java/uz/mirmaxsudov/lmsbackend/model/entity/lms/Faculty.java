package uz.mirmaxsudov.lmsbackend.model.entity.lms;

import jakarta.persistence.Entity;
import lombok.*;
import uz.mirmaxsudov.lmsbackend.model.entity.base.BaseEntity;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Faculty extends BaseEntity {
    private String name;
    private String description;
}
