package uz.mirmaxsudov.lmsbackend.model.entity.auth;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.*;
import uz.mirmaxsudov.lmsbackend.model.entity.base.BaseEntity;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Permission extends BaseEntity {
    @Column(unique = true, nullable = false)
    private String code;
    private String description;
}