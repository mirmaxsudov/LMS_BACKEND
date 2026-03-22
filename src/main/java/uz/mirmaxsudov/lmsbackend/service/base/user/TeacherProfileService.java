package uz.mirmaxsudov.lmsbackend.service.base.user;

import org.springframework.http.ResponseEntity;
import uz.mirmaxsudov.lmsbackend.model.entity.user.TeacherProfile;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.TeacherPosition;
import uz.mirmaxsudov.lmsbackend.model.response.ApiPaginateResponse;
import uz.mirmaxsudov.lmsbackend.model.response.user.user.TeacherProfileResponse;
import uz.mirmaxsudov.lmsbackend.service.base.BaseCRUDService;

import java.util.List;

public interface TeacherProfileService extends BaseCRUDService<TeacherProfile> {
    ResponseEntity<ApiPaginateResponse<List<TeacherProfileResponse>>> getTeacherProfilePaginateResponse(int page, int size, String search, TeacherPosition position);
}