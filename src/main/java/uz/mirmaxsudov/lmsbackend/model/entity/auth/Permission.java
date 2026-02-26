package uz.mirmaxsudov.lmsbackend.model.entity.auth;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import uz.mirmaxsudov.lmsbackend.model.entity.base.BaseEntity;
import uz.mirmaxsudov.lmsbackend.model.enums.auth.PermissionCategory;

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
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PermissionCategory category;
}