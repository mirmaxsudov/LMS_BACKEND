package uz.mirmaxsudov.lmsbackend.model.entity.auth;

import jakarta.persistence.*;
import lombok.*;
import uz.mirmaxsudov.lmsbackend.model.entity.content.Attachment;
import uz.mirmaxsudov.lmsbackend.model.entity.base.BaseEntity;
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
@Table(name = "users",
        indexes = {
                @Index(columnList = "email", unique = true),
                @Index(columnList = "id"),
                @Index(columnList = "profile_image_attachment_id"),
                @Index(columnList = "profile_background_attachment_id")}
)
public class User extends BaseEntity {
    @Column(nullable = false)
    private String firstName;
    @Column(nullable = false)
    private String lastName;
    private String middleName;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    private LocalDateTime brithDate;
    private String phoneNumber;
    @Column(unique = true, nullable = false)
    private String email;
    @Column(nullable = false)
    private String password;
    @Enumerated(EnumType.STRING)
    private UserStatus status = UserStatus.ACTIVE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_image_attachment_id")
    private Attachment profileImageAttachment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_background_attachment_id")
    private Attachment profileBackgroundAttachment;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();
}
