package uz.mirmaxsudov.lmsbackend.service.base.user;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import uz.mirmaxsudov.lmsbackend.model.entity.user.TeacherProfile;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.TeacherPosition;
import uz.mirmaxsudov.lmsbackend.model.request.user.TeacherProfileRequest;
import uz.mirmaxsudov.lmsbackend.model.response.ApiPaginateResponse;
import uz.mirmaxsudov.lmsbackend.model.response.ApiResponse;
import uz.mirmaxsudov.lmsbackend.model.response.user.user.TeacherProfileResponse;
import uz.mirmaxsudov.lmsbackend.security.service.CustomUserDetails;
import uz.mirmaxsudov.lmsbackend.service.base.BaseCRUDService;

import java.util.List;

public interface TeacherProfileService extends BaseCRUDService<TeacherProfile> {
    ResponseEntity<ApiPaginateResponse<List<TeacherProfileResponse>>> getTeacherProfilePaginateResponse(int page, int size, String search, TeacherPosition position);

    ResponseEntity<ApiResponse<TeacherProfileResponse>> postTeacherProfile(
            @Valid TeacherProfileRequest request,
            MultipartFile profileImage,
            MultipartFile profileBackgroundAttachment,
            CustomUserDetails details
    );
}
