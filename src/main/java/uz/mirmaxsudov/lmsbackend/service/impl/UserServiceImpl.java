package uz.mirmaxsudov.lmsbackend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import uz.mirmaxsudov.lmsbackend.common.filter.PageableBuilder;
import uz.mirmaxsudov.lmsbackend.common.util.mappers.AuthMeMapper;
import uz.mirmaxsudov.lmsbackend.common.util.mappers.RoleMapper;
import uz.mirmaxsudov.lmsbackend.common.util.mappers.UserMapper;
import uz.mirmaxsudov.lmsbackend.exceptions.CustomBadRequestException;
import uz.mirmaxsudov.lmsbackend.exceptions.CustomConflictException;
import uz.mirmaxsudov.lmsbackend.exceptions.CustomNotFoundException;
import uz.mirmaxsudov.lmsbackend.model.entity.auth.Role;
import uz.mirmaxsudov.lmsbackend.model.entity.auth.User;
import uz.mirmaxsudov.lmsbackend.model.entity.content.Attachment;
import uz.mirmaxsudov.lmsbackend.model.enums.auth.SystemRole;
import uz.mirmaxsudov.lmsbackend.model.enums.auth.UserStatus;
import uz.mirmaxsudov.lmsbackend.model.enums.content.AttachmentType;
import uz.mirmaxsudov.lmsbackend.model.request.auth.UserRolesUpdateRequest;
import uz.mirmaxsudov.lmsbackend.model.request.user.UserCreateRequest;
import uz.mirmaxsudov.lmsbackend.model.response.ApiPaginateResponse;
import uz.mirmaxsudov.lmsbackend.model.response.ApiResponse;
import uz.mirmaxsudov.lmsbackend.model.response.auth.AuthMe;
import uz.mirmaxsudov.lmsbackend.model.response.auth.AuthMeRole;
import uz.mirmaxsudov.lmsbackend.model.response.user.user.UserPreview;
import uz.mirmaxsudov.lmsbackend.repository.UserRepository;
import uz.mirmaxsudov.lmsbackend.repository.user.specification.UserSpecification;
import uz.mirmaxsudov.lmsbackend.repository.user.specification.dto.UserFilter;
import uz.mirmaxsudov.lmsbackend.security.service.CustomUserDetails;
import uz.mirmaxsudov.lmsbackend.service.base.AttachmentService;
import uz.mirmaxsudov.lmsbackend.service.base.RoleService;
import uz.mirmaxsudov.lmsbackend.service.base.UserService;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final AttachmentService attachmentService;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> getUserByEmailWithAuthorities(String email) {
        return userRepository.findByEmailWithAuthorities(email);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<ApiPaginateResponse<List<UserPreview>>> getAll(
            int page,
            int size,
            String search,
            SystemRole role,
            UserStatus status,
            UUID permissionId
    ) {
        int normalizedPage = Math.max(page - 1, 0);
        int normalizedSize = size <= 0 ? 10 : size;

        Pageable pageable = PageableBuilder.build(normalizedPage, normalizedSize);
        Specification<User> specification = UserSpecification.filter(UserFilter.builder()
                .search(search)
                .role(role)
                .status(status)
                .permissionId(permissionId)
                .build());

        Page<User> users = userRepository.findAll(specification, pageable);
        List<UserPreview> results = users.getContent().stream()
                .map(UserMapper::toPreview)
                .toList();

        return ResponseEntity.ok(ApiPaginateResponse.<List<UserPreview>>builder()
                .success(true)
                .message("Users fetched successfully")
                .results(results)
                .total((int) users.getTotalElements())
                .page(users.getNumber() + 1)
                .size(users.getSize())
                .hasNext(users.hasNext())
                .build());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> getById(UUID userId) {
        return userRepository.findByIdAndDeletedFalse(userId);
    }

    @Override
    @Transactional
    public User saveOrUpdate(User user) {
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User createUserEntity(
            UserCreateRequest request,
            MultipartFile profileImage,
            MultipartFile profileBackgroundAttachment,
            CustomUserDetails details
    ) {
        if (request == null)
            throw new CustomBadRequestException("Request body is required");

        String normalizedEmail = request.getEmail() == null ? null : request.getEmail().trim().toLowerCase();
        if (normalizedEmail == null || normalizedEmail.isBlank())
            throw new CustomBadRequestException("Email is required");

        if (userRepository.findByEmail(normalizedEmail).isPresent())
            throw new CustomConflictException("Email already exists: " + normalizedEmail);

        Set<Role> roles = request.getRoles() == null || request.getRoles().isEmpty()
                ? Set.of()
                : roleService.resolveRoles(request.getRoles());

        Attachment profileImageAttachment = null;
        Attachment profileBackgroundImageAttachment = null;

        if (profileImage != null && !profileImage.isEmpty())
            profileImageAttachment = attachmentService.upload(profileImage, AttachmentType.IMAGE, details == null ? null : details.user());

        if (profileBackgroundAttachment != null && !profileBackgroundAttachment.isEmpty())
            profileBackgroundImageAttachment = attachmentService.upload(
                    profileBackgroundAttachment,
                    AttachmentType.IMAGE,
                    details == null ? null : details.user()
            );

        User savedUser;
        try {
            User user = User.builder()
                    .firstName(request.getFirstName().trim())
                    .lastName(request.getLastName().trim())
                    .middleName(request.getMiddleName() == null ? null : request.getMiddleName().trim())
                    .phoneNumber(request.getPhoneNumber().trim())
                    .gender(request.getGender())
                    .email(normalizedEmail)
                    .password(passwordEncoder.encode(request.getPassword()))
                    .status(request.getStatus() == null ? UserStatus.ACTIVE : request.getStatus())
                    .profileImage(profileImageAttachment)
                    .profileBackgroundImage(profileBackgroundImageAttachment)
                    .roles(roles)
                    .build();

            savedUser = userRepository.save(user);
        } catch (RuntimeException exception) {
            if (profileImageAttachment != null)
                attachmentService.delete(profileImageAttachment.getId());
            if (profileBackgroundImageAttachment != null)
                attachmentService.delete(profileBackgroundImageAttachment.getId());
            throw exception;
        }

        return savedUser;
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponse<AuthMe>> createUser(
            UserCreateRequest request,
            MultipartFile profileImage,
            MultipartFile profileBackgroundAttachment,
            CustomUserDetails details
    ) {
        User savedUser = createUserEntity(request, profileImage, profileBackgroundAttachment, details);

        return ResponseEntity.ok(ApiResponse.<AuthMe>builder()
                .success(true)
                .message("User created successfully")
                .data(AuthMeMapper.toResponse(savedUser))
                .build());
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<Set<AuthMeRole>>> getUserRoles(UUID userId) {
        User user = findActiveUserWithRoles(userId);

        return ResponseEntity.ok(ApiResponse.<Set<AuthMeRole>>builder()
                .success(true)
                .message("User roles fetched successfully")
                .data(RoleMapper.toResponses(user.getRoles()))
                .build());
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponse<AuthMe>> updateUserRoles(UUID userId, UserRolesUpdateRequest request) {
        User user = findActiveUserWithRoles(userId);
        Set<Role> roles = request == null ? Set.of() : roleService.resolveRoleIds(request.getRoleIds());

        ensureNotRemovingLastSuperAdmin(user, roles);
        user.setRoles(roles);
        User updatedUser = userRepository.save(user);

        return ResponseEntity.ok(ApiResponse.<AuthMe>builder()
                .success(true)
                .message("User roles updated successfully")
                .data(AuthMeMapper.toResponse(updatedUser))
                .build());
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponse<AuthMe>> addUserRole(UUID userId, UUID roleId) {
        User user = findActiveUserWithRoles(userId);
        Role role = roleService.resolveRoleIds(Set.of(roleId)).iterator().next();

        user.getRoles().add(role);
        User updatedUser = userRepository.save(user);

        return ResponseEntity.ok(ApiResponse.<AuthMe>builder()
                .success(true)
                .message("User role added successfully")
                .data(AuthMeMapper.toResponse(updatedUser))
                .build());
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponse<AuthMe>> removeUserRole(UUID userId, UUID roleId) {
        User user = findActiveUserWithRoles(userId);
        Role role = roleService.resolveRoleIds(Set.of(roleId)).iterator().next();

        Set<Role> updatedRoles = user.getRoles().stream()
                .filter(existingRole -> !existingRole.getId().equals(role.getId()))
                .collect(java.util.stream.Collectors.toSet());
        ensureNotRemovingLastSuperAdmin(user, updatedRoles);

        user.getRoles().removeIf(existingRole -> existingRole.getId().equals(role.getId()));
        User updatedUser = userRepository.save(user);

        return ResponseEntity.ok(ApiResponse.<AuthMe>builder()
                .success(true)
                .message("User role removed successfully")
                .data(AuthMeMapper.toResponse(updatedUser))
                .build());
    }

    private User findActiveUserWithRoles(UUID userId) {
        return userRepository.findByIdWithRolesAndPermissions(userId)
                .orElseThrow(() -> new CustomNotFoundException("User not found with id: " + userId));
    }

    private void ensureNotRemovingLastSuperAdmin(User user, Set<Role> nextRoles) {
        boolean currentlySuperAdmin = hasRole(user.getRoles(), SystemRole.SUPER_ADMIN.name());
        boolean remainsSuperAdmin = hasRole(nextRoles, SystemRole.SUPER_ADMIN.name());

        if (!currentlySuperAdmin || remainsSuperAdmin)
            return;

        long activeSuperAdmins = userRepository.countActiveUsersByRoleName(SystemRole.SUPER_ADMIN.name());
        if (activeSuperAdmins <= 1)
            throw new CustomBadRequestException("Cannot remove the last active SUPER_ADMIN role");
    }

    private boolean hasRole(Set<Role> roles, String roleName) {
        return roles != null && roles.stream()
                .anyMatch(role -> role != null
                        && !role.isDeleted()
                        && role.getName() != null
                        && role.getName().equalsIgnoreCase(roleName));
    }
}
