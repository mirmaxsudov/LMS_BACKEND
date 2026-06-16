package uz.mirmaxsudov.lmsbackend.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uz.mirmaxsudov.lmsbackend.common.util.APIUtil;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.GroupScheduleType;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.GroupStatus;
import uz.mirmaxsudov.lmsbackend.model.response.ApiPaginateResponse;
import uz.mirmaxsudov.lmsbackend.model.response.ApiResponse;
import uz.mirmaxsudov.lmsbackend.model.response.lms.StudentGroupResponse;
import uz.mirmaxsudov.lmsbackend.model.response.lms.StudyGroupsOverviewResponse;
import uz.mirmaxsudov.lmsbackend.security.service.CustomUserDetails;
import uz.mirmaxsudov.lmsbackend.service.base.user.StudentProfileService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(APIUtil.API_BASE_URL + "students/me/study-groups")
public class StudentStudyGroupController {
    private final StudentProfileService studentProfileService;

    @GetMapping
    public ResponseEntity<ApiPaginateResponse<List<StudentGroupResponse>>> getMyStudyGroups(
            @AuthenticationPrincipal CustomUserDetails details,
            @RequestParam(value = "page", defaultValue = "1", required = false) int page,
            @RequestParam(value = "size", defaultValue = "10", required = false) int size,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "status", required = false) GroupStatus status,
            @RequestParam(value = "active", required = false) Boolean active,
            @RequestParam(value = "scheduleType", required = false) GroupScheduleType scheduleType,
            @RequestParam(value = "courseId", required = false) UUID courseId
    ) {
        return studentProfileService.getMyGroups(details, page, size, search, status, active, scheduleType, courseId);
    }

    @GetMapping("overview")
    public ResponseEntity<ApiResponse<StudyGroupsOverviewResponse>> getMyStudyGroupsOverview(
            @AuthenticationPrincipal CustomUserDetails details
    ) {
        return studentProfileService.getMyStudyGroupsOverview(details);
    }
}
