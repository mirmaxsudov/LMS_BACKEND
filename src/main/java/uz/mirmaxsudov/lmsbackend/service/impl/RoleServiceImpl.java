package uz.mirmaxsudov.lmsbackend.service.impl;

import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.mirmaxsudov.lmsbackend.common.filter.PageableBuilder;
import uz.mirmaxsudov.lmsbackend.common.util.mappers.RoleMapper;
import uz.mirmaxsudov.lmsbackend.exceptions.CustomBadRequestException;
import uz.mirmaxsudov.lmsbackend.exceptions.CustomConflictException;
import uz.mirmaxsudov.lmsbackend.exceptions.CustomNotFoundException;
import uz.mirmaxsudov.lmsbackend.model.entity.auth.Permission;
import uz.mirmaxsudov.lmsbackend.model.entity.auth.Role;
import uz.mirmaxsudov.lmsbackend.model.enums.auth.SystemRole;
import uz.mirmaxsudov.lmsbackend.model.request.auth.RoleCreateRequest;
import uz.mirmaxsudov.lmsbackend.model.request.auth.RoleRequest;
import uz.mirmaxsudov.lmsbackend.model.request.auth.RoleUpdateRequest;
import uz.mirmaxsudov.lmsbackend.model.response.ApiPaginateResponse;
import uz.mirmaxsudov.lmsbackend.model.response.ApiResponse;
import uz.mirmaxsudov.lmsbackend.model.response.auth.AuthMeRole;
import uz.mirmaxsudov.lmsbackend.repository.auth.RoleRepository;
import uz.mirmaxsudov.lmsbackend.service.base.PermissionService;
import uz.mirmaxsudov.lmsbackend.service.base.RoleService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;
    private final PermissionService permissionService;

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<ApiPaginateResponse<List<AuthMeRole>>> getAll(
            int page,
            int size,
            String search,
            UUID permissionId
    ) {
        int normalizedPage = Math.max(page - 1, 0);
        int normalizedSize = size <= 0 ? 10 : size;

        Pageable pageable = PageableBuilder.build(normalizedPage, normalizedSize);
        Page<Role> roles = roleRepository.findAll(buildFilter(search, permissionId), pageable);
        List<AuthMeRole> results = roles.getContent().stream()
                .map(RoleMapper::toResponse)
                .toList();

        return ResponseEntity.ok(ApiPaginateResponse.<List<AuthMeRole>>builder()
                .success(true)
                .message("Roles fetched successfully")
                .results(results)
                .total((int) roles.getTotalElements())
                .page(roles.getNumber() + 1)
                .size(roles.getSize())
                .hasNext(roles.hasNext())
                .build());
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<AuthMeRole>> getByIdResponse(UUID id) {
        Role role = findActiveRoleWithPermissions(id);

        return ResponseEntity.ok(ApiResponse.<AuthMeRole>builder()
                .success(true)
                .message("Role fetched successfully")
                .data(RoleMapper.toResponse(role))
                .build());
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponse<AuthMeRole>> createRole(RoleCreateRequest request) {
        String name = normalizeName(request.getName());
        validateUniqueName(name, null);

        Set<Permission> permissions = permissionService.resolvePermissionIds(request.getPermissionIds());
        Role role = Role.builder()
                .name(name)
                .description(normalizeDescription(request.getDescription()))
                .permissions(permissions)
                .build();

        Role savedRole = roleRepository.save(role);

        return ResponseEntity.ok(ApiResponse.<AuthMeRole>builder()
                .success(true)
                .message("Role created successfully")
                .data(RoleMapper.toResponse(savedRole))
                .build());
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponse<AuthMeRole>> updateRole(UUID id, RoleUpdateRequest request) {
        Role role = findActiveRoleWithPermissions(id);
        String name = normalizeName(request.getName());

        if (isReservedSystemRole(role.getName()) && !role.getName().equals(name))
            throw new CustomBadRequestException("System role name cannot be changed");

        validateUniqueName(name, id);

        role.setName(name);
        role.setDescription(normalizeDescription(request.getDescription()));
        role.setPermissions(permissionService.resolvePermissionIds(request.getPermissionIds()));

        Role updatedRole = roleRepository.save(role);

        return ResponseEntity.ok(ApiResponse.<AuthMeRole>builder()
                .success(true)
                .message("Role updated successfully")
                .data(RoleMapper.toResponse(updatedRole))
                .build());
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponse<Void>> deleteRole(UUID id) {
        Role role = findActiveRoleWithPermissions(id);

        if (isReservedSystemRole(role.getName()))
            throw new CustomBadRequestException("System role cannot be deleted");

        if (roleRepository.existsAssignedToActiveUser(id))
            throw new CustomConflictException("Role is assigned to at least one active user");

        role.getPermissions().clear();
        role.setDeleted(true);
        role.setDeletedAt(LocalDateTime.now());
        roleRepository.save(role);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Role deleted successfully")
                .build());
    }

    @Override
    @Transactional(readOnly = true)
    public Set<Role> resolveRoleIds(Collection<UUID> roleIds) {
        if (roleIds == null || roleIds.isEmpty())
            return new LinkedHashSet<>();

        Set<UUID> requestedIds = roleIds.stream()
                .filter(id -> id != null)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        if (requestedIds.size() != roleIds.size())
            throw new CustomBadRequestException("Role id is required");

        Set<Role> roles = roleRepository.findAllWithPermissionsByIdInAndDeletedFalse(requestedIds);
        Set<UUID> foundIds = roles.stream()
                .map(Role::getId)
                .collect(Collectors.toSet());

        requestedIds.stream()
                .filter(id -> !foundIds.contains(id))
                .findFirst()
                .ifPresent(id -> {
                    throw new CustomNotFoundException("Role not found with id: " + id);
                });

        return roles;
    }

    @Override
    @Transactional(readOnly = true)
    public Set<Role> resolveRoles(List<RoleRequest> roleRequests) {
        if (roleRequests == null || roleRequests.isEmpty())
            throw new CustomBadRequestException("At least one role is required");

        return resolveRoleIds(roleRequests.stream()
                .map(roleRequest -> roleRequest == null ? null : roleRequest.getId())
                .toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Set<Role> getAllByUser(UUID userId) {
        return roleRepository.findAllWithPermissionByUserId(userId);
    }

    private Role findActiveRoleWithPermissions(UUID id) {
        return roleRepository.findWithPermissionsByIdAndDeletedFalse(id)
                .orElseThrow(() -> new CustomNotFoundException("Role not found with id: " + id));
    }

    private Specification<Role> buildFilter(String search, UUID permissionId) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new java.util.ArrayList<>();
            query.distinct(true);
            predicates.add(cb.equal(root.get("deleted"), Boolean.FALSE));

            if (permissionId != null)
                predicates.add(cb.equal(root.join("permissions", JoinType.LEFT).get("id"), permissionId));

            if (search != null && !search.isBlank()) {
                String pattern = "%" + search.trim().toLowerCase(Locale.ROOT) + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("name")), pattern),
                        cb.like(cb.lower(root.get("description")), pattern)
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private void validateUniqueName(String name, UUID currentId) {
        boolean exists = currentId == null
                ? roleRepository.existsByNameIgnoreCaseAndDeletedFalse(name)
                : roleRepository.existsByNameIgnoreCaseAndIdNotAndDeletedFalse(name, currentId);

        if (exists)
            throw new CustomConflictException("Role already exists with name: " + name);
    }

    private boolean isReservedSystemRole(String name) {
        if (name == null)
            return false;

        try {
            SystemRole.valueOf(name);
            return true;
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }

    private String normalizeName(String name) {
        if (name == null || name.isBlank())
            throw new CustomBadRequestException("Role name is required");

        return name.trim()
                .replace('-', '_')
                .replace(' ', '_')
                .toUpperCase(Locale.ROOT);
    }

    private String normalizeDescription(String description) {
        if (description == null)
            return null;

        String trimmedDescription = description.trim();
        return trimmedDescription.isBlank() ? null : trimmedDescription;
    }
}
