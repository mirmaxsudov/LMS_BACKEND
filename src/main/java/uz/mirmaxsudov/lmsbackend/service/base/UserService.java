package uz.mirmaxsudov.lmsbackend.service.base;

import jakarta.validation.Valid;
import uz.mirmaxsudov.lmsbackend.model.entity.auth.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import uz.mirmaxsudov.lmsbackend.model.enums.auth.SystemRole;
import uz.mirmaxsudov.lmsbackend.model.enums.auth.UserStatus;
import uz.mirmaxsudov.lmsbackend.model.request.user.UserCreateRequest;
import uz.mirmaxsudov.lmsbackend.model.response.ApiPaginateResponse;
import uz.mirmaxsudov.lmsbackend.model.response.ApiResponse;
import uz.mirmaxsudov.lmsbackend.model.response.auth.AuthMe;
import uz.mirmaxsudov.lmsbackend.model.response.user.user.UserPreview;
import uz.mirmaxsudov.lmsbackend.security.service.CustomUserDetails;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    Optional<User> getUserByEmail(String email);

    ResponseEntity<ApiPaginateResponse<List<UserPreview>>> getAll(
            int page,
            int size,
            String search,
            SystemRole role,
            UserStatus status,
            UUID permissionId
    );

    Optional<User> getById(UUID userId);

    User saveOrUpdate(User user);

    ResponseEntity<ApiResponse<AuthMe>> createUser(
            @Valid UserCreateRequest request,
            MultipartFile profileImage,
            MultipartFile profileBackgroundAttachment,
            CustomUserDetails details
    );
}
