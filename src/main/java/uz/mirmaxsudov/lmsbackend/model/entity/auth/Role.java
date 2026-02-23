package uz.mirmaxsudov.lmsbackend.model.entity.auth;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.mirmaxsudov.lmsbackend.model.entity.base.BaseEntity;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Role extends BaseEntity {
    @Column(unique = true, nullable = false)
    private String name;
    private String description;
    @ManyToOne
    @JoinColumn(name = "parent_role_id")
    private Role parent;
    @OneToMany
    @JoinTable(
            name = "role_permissions",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private Set<Permission> permissions = new HashSet<>();
}