package uz.mirmaxsudov.lmsbackend.model.entity.auth;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.mirmaxsudov.lmsbackend.model.entity.base.BaseEntity;
import uz.mirmaxsudov.lmsbackend.model.entity.content.Attachment;
import uz.mirmaxsudov.lmsbackend.model.enums.Gender;
import uz.mirmaxsudov.lmsbackend.model.enums.auth.UserStatus;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User extends BaseEntity {

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    private String middleName;

    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private LocalDateTime birthDate;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_image_id")
    private Attachment profileImage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_bg_image_id")
    private Attachment profileBackgroundImage;

    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();
}
