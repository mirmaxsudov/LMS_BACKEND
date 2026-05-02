package uz.mirmaxsudov.lmsbackend.service.base.lms;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.Schedule;
import uz.mirmaxsudov.lmsbackend.model.request.lms.ScheduleCreateRequest;
import uz.mirmaxsudov.lmsbackend.model.request.lms.ScheduleUpdateRequest;
import uz.mirmaxsudov.lmsbackend.model.response.ApiPaginateResponse;
import uz.mirmaxsudov.lmsbackend.model.response.ApiResponse;
import uz.mirmaxsudov.lmsbackend.model.response.lms.ScheduleResponse;
import uz.mirmaxsudov.lmsbackend.service.base.BaseCRUDService;

import java.time.DayOfWeek;
import java.util.List;
import java.util.UUID;

public interface ScheduleService extends BaseCRUDService<Schedule> {
    ResponseEntity<ApiPaginateResponse<List<ScheduleResponse>>> getAll(
            int page,
            int size,
            UUID groupId,
            DayOfWeek dayOfWeek
    );

    ResponseEntity<ApiResponse<ScheduleResponse>> getByIdResponse(UUID id);

    ResponseEntity<ApiResponse<ScheduleResponse>> createSchedule(@Valid ScheduleCreateRequest request);

    ResponseEntity<ApiResponse<ScheduleResponse>> updateSchedule(UUID id, @Valid ScheduleUpdateRequest request);

    ResponseEntity<ApiResponse<Void>> deleteSchedule(UUID id);
}
