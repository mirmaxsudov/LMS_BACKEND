package uz.mirmaxsudov.lmsbackend.model.request.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import uz.mirmaxsudov.lmsbackend.model.enums.auth.PermissionCategory;

@Getter
@Setter
public class PermissionCreateRequest {
    @NotBlank(message = "Permission code is required")
    @Size(max = 150, message = "Permission code must be at most 150 characters")
    private String code;

    @Size(max = 1000, message = "Permission description must be at most 1000 characters")
    private String description;

    @Size(max = 100, message = "Permission module must be at most 100 characters")
    private String module;

    @Size(max = 100, message = "Permission action must be at most 100 characters")
    private String action;

    @NotNull(message = "Permission category is required")
    private PermissionCategory category;

    private Boolean isSystem = Boolean.FALSE;
}
