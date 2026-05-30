package uz.mirmaxsudov.lmsbackend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.mirmaxsudov.lmsbackend.model.entity.auth.Permission;
import uz.mirmaxsudov.lmsbackend.repository.auth.PermissionRepository;
import uz.mirmaxsudov.lmsbackend.service.base.PermissionService;

import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {
    private final PermissionRepository permissionRepository;

    @Override
    public Set<Permission> getAllByUser(UUID userId) {
        return permissionRepository.getAllByUserId(userId);
    }
}