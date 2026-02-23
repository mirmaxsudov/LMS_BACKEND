package uz.mirmaxsudov.lmsbackend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.mirmaxsudov.lmsbackend.model.entity.auth.Permission;
import uz.mirmaxsudov.lmsbackend.model.entity.auth.Role;
import uz.mirmaxsudov.lmsbackend.service.base.PermissionService;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {
    @Override
    public Set<Permission> resolvePermissions(Role role) {
        Set<Permission> permissions = new HashSet<>(role.getPermissions());

        Role parent = role.getParent();

        while (parent != null) {
            permissions.addAll(parent.getPermissions());
            parent = parent.getParent();
        }

        return permissions;
    }
}