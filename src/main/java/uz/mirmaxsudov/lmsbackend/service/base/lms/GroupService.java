package uz.mirmaxsudov.lmsbackend.service.base.lms;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.Group;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.GroupStatus;
import uz.mirmaxsudov.lmsbackend.model.request.lms.GroupCreateRequest;
import uz.mirmaxsudov.lmsbackend.model.request.lms.GroupStartRequest;
import uz.mirmaxsudov.lmsbackend.model.request.lms.GroupUpdateRequest;
import uz.mirmaxsudov.lmsbackend.model.response.ApiPaginateResponse;
import uz.mirmaxsudov.lmsbackend.model.response.ApiResponse;
import uz.mirmaxsudov.lmsbackend.model.response.lms.GroupResponse;
import uz.mirmaxsudov.lmsbackend.model.response.lms.GroupStartResponse;
import uz.mirmaxsudov.lmsbackend.service.base.BaseCRUDService;

import java.util.List;
import java.util.UUID;

public interface GroupService extends BaseCRUDService<Group> {
    ResponseEntity<ApiPaginateResponse<List<GroupResponse>>> getAll(
            int page,
            int size,
            String search,
            UUID courseId,
            UUID teacherId,
            GroupStatus status,
            Boolean active,
            Integer minCapacity,
            Integer maxCapacity
    );

    ResponseEntity<ApiResponse<GroupResponse>> getByIdResponse(UUID id);

    ResponseEntity<ApiResponse<GroupResponse>> createGroup(@Valid GroupCreateRequest request);

    ResponseEntity<ApiResponse<GroupResponse>> updateGroup(UUID id, @Valid GroupUpdateRequest request);

    ResponseEntity<ApiResponse<GroupStartResponse>> startGroup(UUID id, @Valid GroupStartRequest request);

    ResponseEntity<ApiResponse<Void>> deleteGroup(UUID id);
}
