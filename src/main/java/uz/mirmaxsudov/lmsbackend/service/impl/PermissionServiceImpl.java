package uz.mirmaxsudov.lmsbackend.service.impl;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.mirmaxsudov.lmsbackend.common.filter.PageableBuilder;
import uz.mirmaxsudov.lmsbackend.common.util.mappers.PermissionMapper;
import uz.mirmaxsudov.lmsbackend.exceptions.CustomBadRequestException;
import uz.mirmaxsudov.lmsbackend.exceptions.CustomConflictException;
import uz.mirmaxsudov.lmsbackend.exceptions.CustomNotFoundException;
import uz.mirmaxsudov.lmsbackend.model.entity.auth.Permission;
import uz.mirmaxsudov.lmsbackend.model.enums.auth.PermissionCategory;
import uz.mirmaxsudov.lmsbackend.model.request.auth.PermissionCreateRequest;
import uz.mirmaxsudov.lmsbackend.model.request.auth.PermissionUpdateRequest;
import uz.mirmaxsudov.lmsbackend.model.response.ApiPaginateResponse;
import uz.mirmaxsudov.lmsbackend.model.response.ApiResponse;
import uz.mirmaxsudov.lmsbackend.model.response.auth.AuthMePermission;
import uz.mirmaxsudov.lmsbackend.repository.auth.PermissionRepository;
import uz.mirmaxsudov.lmsbackend.repository.auth.RoleRepository;
import uz.mirmaxsudov.lmsbackend.service.base.PermissionService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {
    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<ApiPaginateResponse<List<AuthMePermission>>> getAll(
            int page,
            int size,
            String search,
            String module,
            String action,
            PermissionCategory category,
            Boolean isSystem
    ) {
        int normalizedPage = Math.max(page - 1, 0);
        int normalizedSize = size <= 0 ? 10 : size;

        Pageable pageable = PageableBuilder.build(normalizedPage, normalizedSize);
        Page<Permission> permissions = permissionRepository.findAll(
                buildFilter(search, module, action, category, isSystem),
                pageable
        );

        List<AuthMePermission> results = permissions.getContent().stream()
                .map(PermissionMapper::toResponse)
                .toList();

        return ResponseEntity.ok(ApiPaginateResponse.<List<AuthMePermission>>builder()
                .success(true)
                .message("Permissions fetched successfully")
                .results(results)
                .total((int) permissions.getTotalElements())
                .page(permissions.getNumber() + 1)
                .size(permissions.getSize())
                .hasNext(permissions.hasNext())
                .build());
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<AuthMePermission>> getByIdResponse(UUID id) {
        Permission permission = findActivePermission(id);

        return ResponseEntity.ok(ApiResponse.<AuthMePermission>builder()
                .success(true)
                .message("Permission fetched successfully")
                .data(PermissionMapper.toResponse(permission))
                .build());
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponse<AuthMePermission>> createPermission(PermissionCreateRequest request) {
        String code = normalizeCode(request.getCode());
        validateUniqueCode(code, null);

        Permission permission = Permission.builder()
                .code(code)
                .description(normalizeNullable(request.getDescription()))
                .module(normalizeNullable(request.getModule()))
                .action(normalizeNullable(request.getAction()))
                .category(request.getCategory())
                .isSystem(Boolean.TRUE.equals(request.getIsSystem()))
                .build();

        Permission savedPermission = permissionRepository.save(permission);

        return ResponseEntity.ok(ApiResponse.<AuthMePermission>builder()
                .success(true)
                .message("Permission created successfully")
                .data(PermissionMapper.toResponse(savedPermission))
                .build());
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponse<AuthMePermission>> updatePermission(UUID id, PermissionUpdateRequest request) {
        Permission permission = findActivePermission(id);
        String code = normalizeCode(request.getCode());

        if (Boolean.TRUE.equals(permission.getIsSystem()) && !permission.getCode().equals(code))
            throw new CustomBadRequestException("System permission code cannot be changed");

        validateUniqueCode(code, id);

        permission.setCode(code);
        permission.setDescription(normalizeNullable(request.getDescription()));
        permission.setModule(normalizeNullable(request.getModule()));
        permission.setAction(normalizeNullable(request.getAction()));
        permission.setCategory(request.getCategory());
        permission.setIsSystem(Boolean.TRUE.equals(permission.getIsSystem()) || Boolean.TRUE.equals(request.getIsSystem()));

        Permission updatedPermission = permissionRepository.save(permission);

        return ResponseEntity.ok(ApiResponse.<AuthMePermission>builder()
                .success(true)
                .message("Permission updated successfully")
                .data(PermissionMapper.toResponse(updatedPermission))
                .build());
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponse<Void>> deletePermission(UUID id) {
        Permission permission = findActivePermission(id);

        if (Boolean.TRUE.equals(permission.getIsSystem()))
            throw new CustomBadRequestException("System permission cannot be deleted");

        if (roleRepository.existsActiveRoleWithPermission(id))
            throw new CustomConflictException("Permission is assigned to at least one active role");

        permission.setDeleted(true);
        permission.setDeletedAt(LocalDateTime.now());
        permissionRepository.save(permission);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Permission deleted successfully")
                .build());
    }

    @Override
    @Transactional(readOnly = true)
    public Set<Permission> resolvePermissionIds(Collection<UUID> permissionIds) {
        if (permissionIds == null || permissionIds.isEmpty())
            return new LinkedHashSet<>();

        Set<UUID> requestedIds = permissionIds.stream()
                .filter(id -> id != null)
                .collect(java.util.stream.Collectors.toCollection(LinkedHashSet::new));

        if (requestedIds.size() != permissionIds.size())
            throw new CustomBadRequestException("Permission id is required");

        List<Permission> permissions = permissionRepository.findAllByIdInAndDeletedFalse(requestedIds);
        Set<UUID> foundIds = permissions.stream()
                .map(Permission::getId)
                .collect(java.util.stream.Collectors.toSet());

        requestedIds.stream()
                .filter(id -> !foundIds.contains(id))
                .findFirst()
                .ifPresent(id -> {
                    throw new CustomNotFoundException("Permission not found with id: " + id);
                });

        return new LinkedHashSet<>(permissions);
    }

    @Override
    @Transactional(readOnly = true)
    public Set<Permission> getAllByUser(UUID userId) {
        return permissionRepository.getAllByUserId(userId);
    }

    private Permission findActivePermission(UUID id) {
        return permissionRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new CustomNotFoundException("Permission not found with id: " + id));
    }

    private Specification<Permission> buildFilter(
            String search,
            String module,
            String action,
            PermissionCategory category,
            Boolean isSystem
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new java.util.ArrayList<>();
            predicates.add(cb.equal(root.get("deleted"), Boolean.FALSE));

            if (category != null)
                predicates.add(cb.equal(root.get("category"), category));

            if (isSystem != null)
                predicates.add(cb.equal(root.get("isSystem"), isSystem));

            if (module != null && !module.isBlank())
                predicates.add(cb.equal(cb.lower(root.get("module")), module.trim().toLowerCase(Locale.ROOT)));

            if (action != null && !action.isBlank())
                predicates.add(cb.equal(cb.lower(root.get("action")), action.trim().toLowerCase(Locale.ROOT)));

            if (search != null && !search.isBlank()) {
                String pattern = "%" + search.trim().toLowerCase(Locale.ROOT) + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("code")), pattern),
                        cb.like(cb.lower(root.get("description")), pattern),
                        cb.like(cb.lower(root.get("module")), pattern),
                        cb.like(cb.lower(root.get("action")), pattern)
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private void validateUniqueCode(String code, UUID currentId) {
        boolean exists = currentId == null
                ? permissionRepository.existsByCodeIgnoreCaseAndDeletedFalse(code)
                : permissionRepository.existsByCodeIgnoreCaseAndIdNotAndDeletedFalse(code, currentId);

        if (exists)
            throw new CustomConflictException("Permission already exists with code: " + code);
    }

    private String normalizeCode(String code) {
        if (code == null || code.isBlank())
            throw new CustomBadRequestException("Permission code is required");

        return code.trim()
                .replace('-', '_')
                .replace(' ', '_')
                .toUpperCase(Locale.ROOT);
    }

    private String normalizeNullable(String value) {
        if (value == null)
            return null;

        String trimmed = value.trim();
        return trimmed.isBlank() ? null : trimmed;
    }
}
