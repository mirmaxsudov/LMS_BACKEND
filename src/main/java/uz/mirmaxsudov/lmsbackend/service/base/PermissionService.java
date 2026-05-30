package uz.mirmaxsudov.lmsbackend.service.base;

import uz.mirmaxsudov.lmsbackend.model.entity.auth.Permission;

import java.util.Set;
import java.util.UUID;

public interface PermissionService {
    Set<Permission> getAllByUser(UUID userId);
}
