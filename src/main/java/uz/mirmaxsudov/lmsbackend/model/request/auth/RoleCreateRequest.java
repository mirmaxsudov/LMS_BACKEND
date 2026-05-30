package uz.mirmaxsudov.lmsbackend.model.request.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class RoleCreateRequest {
    @NotBlank(message = "Role name is required")
    @Size(max = 100, message = "Role name must be at most 100 characters")
    private String name;

    @Size(max = 1000, message = "Role description must be at most 1000 characters")
    private String description;

    private Set<UUID> permissionIds = new LinkedHashSet<>();
}
