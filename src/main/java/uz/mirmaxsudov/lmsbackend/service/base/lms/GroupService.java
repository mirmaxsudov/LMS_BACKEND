package uz.mirmaxsudov.lmsbackend.service.base.lms;

import org.springframework.http.ResponseEntity;
import uz.mirmaxsudov.lmsbackend.model.request.lms.GroupCreateRequest;
import uz.mirmaxsudov.lmsbackend.model.request.lms.GroupUpdateRequest;
import uz.mirmaxsudov.lmsbackend.model.response.ApiPaginateResponse;
import uz.mirmaxsudov.lmsbackend.model.response.ApiResponse;
import uz.mirmaxsudov.lmsbackend.model.response.lms.GroupResponse;
import uz.mirmaxsudov.lmsbackend.security.service.CustomUserDetails;

import java.util.List;
import java.util.UUID;

public interface GroupService {
    ResponseEntity<ApiResponse<GroupResponse>> create(GroupCreateRequest request, CustomUserDetails details);

    ResponseEntity<ApiResponse<GroupResponse>> getById(UUID id, CustomUserDetails details);

    ResponseEntity<ApiResponse<GroupResponse>> update(UUID id, GroupUpdateRequest request, CustomUserDetails details);

    ResponseEntity<ApiResponse<Void>> delete(UUID id, CustomUserDetails details);

    ResponseEntity<ApiPaginateResponse<List<GroupResponse>>> getPaginate(int page, int size, String search, UUID courseId, UUID teacherId, Boolean active, CustomUserDetails details);
}
