package uz.mirmaxsudov.lmsbackend.controller.lms;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uz.mirmaxsudov.lmsbackend.common.util.APIUtil;
import uz.mirmaxsudov.lmsbackend.model.request.lms.ScheduleCreateRequest;
import uz.mirmaxsudov.lmsbackend.model.request.lms.ScheduleUpdateRequest;
import uz.mirmaxsudov.lmsbackend.model.response.ApiPaginateResponse;
import uz.mirmaxsudov.lmsbackend.model.response.ApiResponse;
import uz.mirmaxsudov.lmsbackend.model.response.lms.ScheduleResponse;
import uz.mirmaxsudov.lmsbackend.service.base.lms.ScheduleService;

import java.time.DayOfWeek;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(APIUtil.API_BASE_URL + "schedules")
public class ScheduleController {
    private final ScheduleService scheduleService;

    @GetMapping
    public ResponseEntity<ApiPaginateResponse<List<ScheduleResponse>>> getAll(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size,
            @RequestParam(value = "groupId", required = false) UUID groupId,
            @RequestParam(value = "dayOfWeek", required = false) DayOfWeek dayOfWeek
    ) {
        return scheduleService.getAll(page, size, groupId, dayOfWeek);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ScheduleResponse>> getById(@PathVariable UUID id) {
        return scheduleService.getByIdResponse(id);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ScheduleResponse>> create(@RequestBody @Valid ScheduleCreateRequest request) {
        return scheduleService.createSchedule(request);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ScheduleResponse>> update(
            @PathVariable UUID id,
            @RequestBody @Valid ScheduleUpdateRequest request
    ) {
        return scheduleService.updateSchedule(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        return scheduleService.deleteSchedule(id);
    }
}
