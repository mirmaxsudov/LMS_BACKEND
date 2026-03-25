package uz.mirmaxsudov.lmsbackend.service.impl.auth;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uz.mirmaxsudov.lmsbackend.common.util.mappers.AuthMeMapper;
import uz.mirmaxsudov.lmsbackend.common.util.mappers.AuthMePatchMapper;
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
        var user = userService.getUserByEmail(request.getUsername())
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
    public ResponseEntity<ApiResponse<AuthMe>> getMe(CustomUserDetails details) {
        User user = details.user();
        return ResponseEntity.ok(ApiResponse.<AuthMe>builder()
                .message("Get me successful")
                .success(true)
                .data(AuthMeMapper.toResponse(user))
                .build());
    }

    @Override
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

        if (hasProfileImagePatch)
            patchProfileImage(profileImage, user);

        if (hasProfileBackgroundPatch)
            patchProfileBackgroundImage(profileBackgroundImage, user);

        userService.saveOrUpdate(user);

        return ResponseEntity.ok(ApiResponse.<AuthMe>builder()
                .message("Profile updated successfully")
                .success(true)
                .data(AuthMeMapper.toResponse(user))
                .build());
    }

    private void patchProfileImage(MultipartFile profileImage, User user) {
        if (profileImage.isEmpty())
            throw new CustomBadRequestException("Profile image file must not be empty");

        Attachment previousAttachment = user.getProfileImageAttachment();
        Attachment uploadedAttachment = attachmentService.upload(profileImage, AttachmentType.IMAGE, user);

        user.setProfileImageAttachment(uploadedAttachment);

        if (previousAttachment != null && !previousAttachment.getId().equals(uploadedAttachment.getId()))
            attachmentService.delete(previousAttachment.getId());
    }

    private void patchProfileBackgroundImage(MultipartFile profileBackgroundImage, User user) {
        if (profileBackgroundImage.isEmpty())
            throw new CustomBadRequestException("Profile background image file must not be empty");

        Attachment previousAttachment = user.getProfileBackgroundAttachment();
        Attachment uploadedAttachment = attachmentService.upload(profileBackgroundImage, AttachmentType.IMAGE, user);

        user.setProfileBackgroundAttachment(uploadedAttachment);

        if (previousAttachment != null && !previousAttachment.getId().equals(uploadedAttachment.getId()))
            attachmentService.delete(previousAttachment.getId());
    }
}
