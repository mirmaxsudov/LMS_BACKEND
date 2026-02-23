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
@Table(name = "users",
        indexes = {
                @Index(columnList = "phoneNumber", unique = true),
                @Index(columnList = "id")}
)
public class User extends BaseEntity {
    @Column(unique = true, nullable = false)
    private String phoneNumber;
    @Column(nullable = false)
    private String password;
    private boolean active = Boolean.TRUE;

    @ManyToMany
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();
}