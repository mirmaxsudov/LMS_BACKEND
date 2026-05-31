package uz.mirmaxsudov.lmsbackend.service.impl.auth;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import uz.mirmaxsudov.lmsbackend.common.util.mappers.AuthMeMapper;
import uz.mirmaxsudov.lmsbackend.common.util.mappers.AuthMePatchMapper;
import uz.mirmaxsudov.lmsbackend.config.security.CacheConfig;
import uz.mirmaxsudov.lmsbackend.exceptions.CustomBadRequestException;
import uz.mirmaxsudov.lmsbackend.exceptions.CustomConflictException;
import uz.mirmaxsudov.lmsbackend.exceptions.CustomNotFoundException;
import uz.mirmaxsudov.lmsbackend.model.entity.auth.User;
import uz.mirmaxsudov.lmsbackend.model.entity.content.Attachment;
import uz.mirmaxsudov.lmsbackend.model.enums.content.AttachmentType;
import uz.mirmaxsudov.lmsbackend.model.request.auth.AuthMeRequest;
import uz.mirmaxsudov.lmsbackend.model.request.auth.LoginRequest;
import uz.mirmaxsudov.lmsbackend.model.response.ApiResponse;
import uz.mirmaxsudov.lmsbackend.model.response.auth.AuthMe;
import uz.mirmaxsudov.lmsbackend.model.response.auth.JwtResponse;
import uz.mirmaxsudov.lmsbackend.security.service.CustomUserDetails;
import uz.mirmaxsudov.lmsbackend.security.service.JwtService;
import uz.mirmaxsudov.lmsbackend.service.base.AttachmentService;
import uz.mirmaxsudov.lmsbackend.service.base.UserService;
import uz.mirmaxsudov.lmsbackend.service.base.auth.AuthService;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final JwtService jwtService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final AuthMePatchMapper authMePatchMapper;
    private final AttachmentService attachmentService;

    @Override
    public ResponseEntity<ApiResponse<JwtResponse>> login(LoginRequest request, HttpServletRequest servletRequest) {
        var user = userService.getUserByEmailWithAuthorities(request.getUsername())
                .orElseThrow(() -> new CustomNotFoundException("User not found with email: " + request.getUsername()));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword()))
            throw new CustomBadRequestException("Invalid password");

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setPath("/");

        JwtResponse jwtResponse = JwtResponse.builder()
                .accessToken(accessToken)
                .build();

        return ResponseEntity.ok(ApiResponse.<JwtResponse>builder()
                .message("Login successful")
                .data(jwtResponse)
                .success(true)
                .build());
    }

    @Override
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletResponse response) {
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .message("Logout successful")
                .success(true)
                .build());
    }

    @Override
    @Cacheable(value = CacheConfig.AUTH_ME, key = "#details.user().id")
    public ResponseEntity<ApiResponse<AuthMe>> getMe(CustomUserDetails details) {
        User user = details.user();

        Attachment profileImage = resolveAttachment(user.getProfileImage());
        Attachment profileBackgroundImage = resolveAttachment(user.getProfileBackgroundImage());

        return ResponseEntity.ok(ApiResponse.<AuthMe>builder()
                .message("Get me successful")
                .success(true)
                .data(AuthMeMapper.toResponse(user, profileImage, profileBackgroundImage))
                .build());
    }

    @Override
    @CacheEvict(
            value = CacheConfig.AUTH_ME, key = "#details.user().id"
    )
    @Transactional
    public ResponseEntity<ApiResponse<AuthMe>> patchMe(
            AuthMeRequest request,
            MultipartFile profileImage,
            MultipartFile profileBackgroundImage,
            CustomUserDetails details
    ) {
        User user = details.user();

        boolean hasRequestPatch = request != null;
        boolean hasProfileImagePatch = profileImage != null;
        boolean hasProfileBackgroundPatch = profileBackgroundImage != null;

        if (!hasRequestPatch && !hasProfileImagePatch && !hasProfileBackgroundPatch)
            throw new CustomBadRequestException("No patch data provided");

        if (hasRequestPatch) {
            if (request.getFirstName() != null && request.getFirstName().isBlank())
                throw new CustomBadRequestException("First name cannot be blank");

            if (request.getLastName() != null && request.getLastName().isBlank())
                throw new CustomBadRequestException("Last name cannot be blank");

            if (request.getEmail() != null) {
                String newEmail = request.getEmail().trim();
                if (newEmail.isBlank())
                    throw new CustomBadRequestException("Email cannot be blank");

                userService.getUserByEmail(newEmail)
                        .filter(existingUser -> !existingUser.getId().equals(user.getId()))
                        .ifPresent(existingUser -> {
                            throw new CustomConflictException("Email already exists: " + newEmail);
                        });
            }

            authMePatchMapper.patch(request, user);
        }

        Attachment previousProfileImage = null;
        Attachment previousProfileBackgroundImage = null;

        if (hasProfileImagePatch)
            previousProfileImage = patchProfileImage(profileImage, user);

        if (hasProfileBackgroundPatch)
            previousProfileBackgroundImage = patchProfileBackgroundImage(profileBackgroundImage, user);

        User savedUser = userService.saveOrUpdate(user);

        deleteReplacedAttachment(previousProfileImage, savedUser.getProfileImage());
        deleteReplacedAttachment(previousProfileBackgroundImage, savedUser.getProfileBackgroundImage());

        return ResponseEntity.ok(ApiResponse.<AuthMe>builder()
                .message("Profile updated successfully")
                .success(true)
                .data(AuthMeMapper.toResponse(savedUser))
                .build());
    }

    protected Attachment patchProfileImage(MultipartFile profileImage, User user) {
        if (profileImage.isEmpty())
            throw new CustomBadRequestException("Profile image file must not be empty");

        Attachment previousAttachment = user.getProfileImage();
        Attachment uploadedAttachment = attachmentService.upload(profileImage, AttachmentType.IMAGE, user);

        user.setProfileImage(uploadedAttachment);
        return previousAttachment;
    }

    protected Attachment patchProfileBackgroundImage(MultipartFile profileBackgroundImage, User user) {
        if (profileBackgroundImage.isEmpty())
            throw new CustomBadRequestException("Profile background image file must not be empty");

        Attachment previousAttachment = user.getProfileBackgroundImage();
        Attachment uploadedAttachment = attachmentService.upload(profileBackgroundImage, AttachmentType.IMAGE, user);

        user.setProfileBackgroundImage(uploadedAttachment);
        return previousAttachment;
    }

    private Attachment resolveAttachment(Attachment attachment) {
        if (attachment == null || attachment.getId() == null)
            return null;
        return attachmentService.getOptionalById(attachment.getId()).orElse(null);
    }

    private void deleteReplacedAttachment(Attachment previousAttachment, Attachment currentAttachment) {
        if (previousAttachment == null || previousAttachment.getId() == null)
            return;
        if (currentAttachment != null && previousAttachment.getId().equals(currentAttachment.getId()))
            return;
        attachmentService.delete(previousAttachment.getId());
    }
}
