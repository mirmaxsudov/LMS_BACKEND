package uz.mirmaxsudov.lmsbackend.service.base;

import uz.mirmaxsudov.lmsbackend.model.entity.auth.Role;
import uz.mirmaxsudov.lmsbackend.model.request.auth.RoleRequest;

import java.util.List;
import java.util.Set;

public interface RoleService {
    Set<Role> resolveRoles(List<RoleRequest> roleRequests);
}
