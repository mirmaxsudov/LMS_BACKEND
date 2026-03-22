package uz.mirmaxsudov.lmsbackend.model.request.auth;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class RoleRequest {
    private UUID id;
    private List<UUID> permissions = new ArrayList<>();
}