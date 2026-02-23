package uz.mirmaxsudov.lmsbackend.service.base;

import uz.mirmaxsudov.lmsbackend.model.entity.auth.Permission;
import uz.mirmaxsudov.lmsbackend.model.entity.auth.Role;

import java.util.Set;

public interface PermissionService {
    Set<Permission> resolvePermissions(Role role);
}
