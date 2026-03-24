package uz.mirmaxsudov.lmsbackend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.mirmaxsudov.lmsbackend.exceptions.CustomBadRequestException;
import uz.mirmaxsudov.lmsbackend.exceptions.CustomNotFoundException;
import uz.mirmaxsudov.lmsbackend.model.entity.auth.Role;
import uz.mirmaxsudov.lmsbackend.model.request.auth.RoleRequest;
import uz.mirmaxsudov.lmsbackend.repository.auth.RoleRepository;
import uz.mirmaxsudov.lmsbackend.service.base.RoleService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;

    @Override
    public Set<Role> resolveRoles(List<RoleRequest> roleRequests) {
        if (roleRequests == null || roleRequests.isEmpty())
            throw new CustomBadRequestException("At least one role is required");

        Set<Role> roles = new HashSet<>();

        for (RoleRequest roleRequest : roleRequests) {
            if (roleRequest == null || roleRequest.getId() == null)
                throw new CustomBadRequestException("Role id is required");

            Role role = roleRepository.findById(roleRequest.getId())
                    .orElseThrow(() -> new CustomNotFoundException("Role not found with id: " + roleRequest.getId()));
            roles.add(role);
        }

        return roles;
    }
}
