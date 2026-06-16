package uz.mirmaxsudov.lmsbackend.service.base.user;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import uz.mirmaxsudov.lmsbackend.model.entity.user.StudentProfile;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.GroupScheduleType;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.GroupStatus;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.StudentStatus;
import uz.mirmaxsudov.lmsbackend.model.request.user.StudentProfileRequest;
import uz.mirmaxsudov.lmsbackend.model.response.ApiPaginateResponse;
import uz.mirmaxsudov.lmsbackend.model.response.ApiResponse;
import uz.mirmaxsudov.lmsbackend.model.response.lms.StudentGroupResponse;
import uz.mirmaxsudov.lmsbackend.model.response.lms.StudyGroupsOverviewResponse;
import uz.mirmaxsudov.lmsbackend.model.response.user.user.StudentProfileResponse;
import uz.mirmaxsudov.lmsbackend.security.service.CustomUserDetails;
import uz.mirmaxsudov.lmsbackend.service.base.BaseCRUDService;

import java.util.List;
import java.util.UUID;

public interface StudentProfileService extends BaseCRUDService<StudentProfile> {
    ResponseEntity<ApiPaginateResponse<List<StudentProfileResponse>>>   getStudentProfilePaginateResponse(int page, int size, String search, StudentStatus status);

    ResponseEntity<ApiResponse<StudentProfileResponse>> postStudentProfile(
            @Valid StudentProfileRequest request,
            MultipartFile profileImage,
            MultipartFile profileBackgroundAttachment,
            CustomUserDetails details
    );

    ResponseEntity<ApiPaginateResponse<List<StudentGroupResponse>>> getMyGroups(
            CustomUserDetails details,
            int page,
            int size,
            String search,
            GroupStatus status,
            Boolean active,
            GroupScheduleType scheduleType,
            UUID courseId
    );

    ResponseEntity<ApiResponse<StudyGroupsOverviewResponse>> getMyStudyGroupsOverview(CustomUserDetails details);
}
