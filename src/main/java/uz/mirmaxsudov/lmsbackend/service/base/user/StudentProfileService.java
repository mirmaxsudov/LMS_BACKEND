package uz.mirmaxsudov.lmsbackend.service.base.user;

import org.springframework.http.ResponseEntity;
import uz.mirmaxsudov.lmsbackend.model.entity.user.StudentProfile;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.StudentStatus;
import uz.mirmaxsudov.lmsbackend.model.response.ApiPaginateResponse;
import uz.mirmaxsudov.lmsbackend.model.response.user.user.StudentProfileResponse;
import uz.mirmaxsudov.lmsbackend.service.base.BaseCRUDService;

import java.util.List;

public interface StudentProfileService extends BaseCRUDService<StudentProfile> {
    ResponseEntity<ApiPaginateResponse<List<StudentProfileResponse>>> getStudentProfilePaginateResponse(int page, int size, String search, StudentStatus status);
}
