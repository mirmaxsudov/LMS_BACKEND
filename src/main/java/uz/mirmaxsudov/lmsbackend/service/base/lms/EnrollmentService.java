package uz.mirmaxsudov.lmsbackend.service.base.lms;

import org.springframework.http.ResponseEntity;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.EnrollmentStatus;
import uz.mirmaxsudov.lmsbackend.model.request.lms.EnrollmentCreateRequest;
import uz.mirmaxsudov.lmsbackend.model.request.lms.EnrollmentUpdateRequest;
import uz.mirmaxsudov.lmsbackend.model.response.ApiPaginateResponse;
import uz.mirmaxsudov.lmsbackend.model.response.ApiResponse;
import uz.mirmaxsudov.lmsbackend.model.response.lms.EnrollmentResponse;
import uz.mirmaxsudov.lmsbackend.security.service.CustomUserDetails;

import java.util.List;
import java.util.UUID;

public interface EnrollmentService {
    ResponseEntity<ApiResponse<EnrollmentResponse>> create(EnrollmentCreateRequest request, CustomUserDetails details);

    ResponseEntity<ApiResponse<EnrollmentResponse>> getById(UUID id, CustomUserDetails details);

    ResponseEntity<ApiResponse<EnrollmentResponse>> update(UUID id, EnrollmentUpdateRequest request, CustomUserDetails details);

    ResponseEntity<ApiResponse<Void>> delete(UUID id, CustomUserDetails details);

    ResponseEntity<ApiPaginateResponse<List<EnrollmentResponse>>> getPaginate(int page, int size, UUID groupId, UUID studentProfileId, EnrollmentStatus status, CustomUserDetails details);
}
