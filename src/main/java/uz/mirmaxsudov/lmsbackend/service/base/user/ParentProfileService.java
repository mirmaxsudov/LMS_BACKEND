package uz.mirmaxsudov.lmsbackend.service.base.user;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import uz.mirmaxsudov.lmsbackend.model.entity.user.ParentProfile;
import uz.mirmaxsudov.lmsbackend.model.request.user.ParentProfileRequest;
import uz.mirmaxsudov.lmsbackend.model.response.ApiPaginateResponse;
import uz.mirmaxsudov.lmsbackend.model.response.ApiResponse;
import uz.mirmaxsudov.lmsbackend.model.response.user.user.ParentProfileResponse;
import uz.mirmaxsudov.lmsbackend.security.service.CustomUserDetails;
import uz.mirmaxsudov.lmsbackend.service.base.BaseCRUDService;

import java.util.List;

public interface ParentProfileService extends BaseCRUDService<ParentProfile> {
    ResponseEntity<ApiPaginateResponse<List<ParentProfileResponse>>> getParentProfilePaginateResponse(int page, int size, String search);

    ResponseEntity<ApiResponse<ParentProfileResponse>> postParentProfile(
            @Valid ParentProfileRequest request,
            MultipartFile profileImage,
            MultipartFile profileBackgroundAttachment,
            CustomUserDetails details
    );
}
