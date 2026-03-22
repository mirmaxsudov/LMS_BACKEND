package uz.mirmaxsudov.lmsbackend.service.base.user;

import org.springframework.http.ResponseEntity;
import uz.mirmaxsudov.lmsbackend.model.entity.user.ParentProfile;
import uz.mirmaxsudov.lmsbackend.model.response.ApiPaginateResponse;
import uz.mirmaxsudov.lmsbackend.model.response.user.user.ParentProfileResponse;
import uz.mirmaxsudov.lmsbackend.service.base.BaseCRUDService;

import java.util.List;

public interface ParentProfileService extends BaseCRUDService<ParentProfile> {
    ResponseEntity<ApiPaginateResponse<List<ParentProfileResponse>>> getParentProfilePaginateResponse(int page, int size, String search);
}
