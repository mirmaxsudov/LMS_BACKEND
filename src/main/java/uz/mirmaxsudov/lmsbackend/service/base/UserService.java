package uz.mirmaxsudov.lmsbackend.service.base;

import uz.mirmaxsudov.lmsbackend.model.entity.auth.User;
import org.springframework.http.ResponseEntity;
import uz.mirmaxsudov.lmsbackend.model.enums.auth.SystemRole;
import uz.mirmaxsudov.lmsbackend.model.enums.auth.UserStatus;
import uz.mirmaxsudov.lmsbackend.model.response.ApiPaginateResponse;
import uz.mirmaxsudov.lmsbackend.model.response.user.user.UserPreview;

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
}
