package uz.mirmaxsudov.lmsbackend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uz.mirmaxsudov.lmsbackend.common.filter.PageableBuilder;
import uz.mirmaxsudov.lmsbackend.common.util.mappers.AuthMeMapper;
import uz.mirmaxsudov.lmsbackend.exceptions.CustomBadRequestException;
import uz.mirmaxsudov.lmsbackend.exceptions.CustomConflictException;
import uz.mirmaxsudov.lmsbackend.model.entity.content.Attachment;
import uz.mirmaxsudov.lmsbackend.model.entity.auth.Role;
import uz.mirmaxsudov.lmsbackend.model.entity.auth.User;
import uz.mirmaxsudov.lmsbackend.model.enums.auth.SystemRole;
import uz.mirmaxsudov.lmsbackend.model.enums.auth.UserStatus;
import uz.mirmaxsudov.lmsbackend.model.enums.content.AttachmentType;
import uz.mirmaxsudov.lmsbackend.model.request.user.UserCreateRequest;
import uz.mirmaxsudov.lmsbackend.model.response.ApiPaginateResponse;
import uz.mirmaxsudov.lmsbackend.model.response.ApiResponse;
import uz.mirmaxsudov.lmsbackend.model.response.auth.AuthMe;
import uz.mirmaxsudov.lmsbackend.model.response.user.user.UserPreview;
import uz.mirmaxsudov.lmsbackend.repository.UserRepository;
import uz.mirmaxsudov.lmsbackend.repository.user.specification.UserSpecification;
import uz.mirmaxsudov.lmsbackend.repository.user.specification.dto.UserFilter;
import uz.mirmaxsudov.lmsbackend.security.service.CustomUserDetails;
import uz.mirmaxsudov.lmsbackend.service.base.AttachmentService;
import uz.mirmaxsudov.lmsbackend.service.base.RoleService;
import uz.mirmaxsudov.lmsbackend.service.base.UserService;

import java.util.LinkedHashSet;
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
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<User> getUserByEmailWithAuthorities(String email) {
        return userRepository.findByEmailWithAuthorities(email);
    }

    @Override
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
                .map(user -> UserPreview.builder()
                        .id(user.getId())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .middleName(user.getMiddleName())
                        .email(user.getEmail())
                        .phoneNumber(user.getPhoneNumber())
                        .status(user.getStatus())
                        .profileImageUrl(user.getProfileImage() == null ? null : user.getProfileImage().getUrl())
                        .profileBackgroundUrl(user.getProfileBackgroundImage() == null ? null : user.getProfileBackgroundImage().getUrl())
                        .roles(mapRoleNames(user.getRoles()))
                        .build())
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
    public Optional<User> getById(UUID userId) {
        return userRepository.findById(userId);
    }

    @Override
    public User saveOrUpdate(User user) {
        return userRepository.save(user);
    }

    @Override
    public ResponseEntity<ApiResponse<AuthMe>> createUser(
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

        Set<Role> roles = roleService.resolveRoles(request.getRoles());
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

        return ResponseEntity.ok(ApiResponse.<AuthMe>builder()
                .success(true)
                .message("User created successfully")
                .data(AuthMeMapper.toResponse(savedUser))
                .build());
    }

    private Set<String> mapRoleNames(Set<Role> roles) {
        if (roles == null || roles.isEmpty()) {
            return Set.of();
        }

        return roles.stream()
                .map(Role::getName)
                .filter(name -> name != null && !name.isBlank())
                .collect(java.util.stream.Collectors.toCollection(LinkedHashSet::new));
    }
}
