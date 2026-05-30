package uz.mirmaxsudov.lmsbackend.service.base;

import uz.mirmaxsudov.lmsbackend.model.entity.auth.Role;
import uz.mirmaxsudov.lmsbackend.model.request.auth.RoleRequest;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface RoleService {
    Set<Role> resolveRoles(List<RoleRequest> roleRequests);

    Set<Role> getAllByUser(UUID userId);
}
