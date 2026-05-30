package uz.mirmaxsudov.lmsbackend.model.response.auth;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import uz.mirmaxsudov.lmsbackend.model.enums.auth.PermissionCategory;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
public class AuthMePermission {
    private UUID id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String code;
    private String description;
    private String module;
    private String action;
    private PermissionCategory category;
    private Boolean isSystem;
}