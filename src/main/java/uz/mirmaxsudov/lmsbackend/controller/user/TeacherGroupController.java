package uz.mirmaxsudov.lmsbackend.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uz.mirmaxsudov.lmsbackend.common.util.APIUtil;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.GroupStatus;
import uz.mirmaxsudov.lmsbackend.model.response.ApiPaginateResponse;
import uz.mirmaxsudov.lmsbackend.model.response.ApiResponse;
import uz.mirmaxsudov.lmsbackend.model.response.lms.TeacherGroupResponse;
import uz.mirmaxsudov.lmsbackend.model.response.lms.TeacherGroupStudentsResponse;
import uz.mirmaxsudov.lmsbackend.security.service.CustomUserDetails;
import uz.mirmaxsudov.lmsbackend.service.base.user.TeacherProfileService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(APIUtil.API_BASE_URL + "teachers")
public class TeacherGroupController {
    private final TeacherProfileService teacherProfileService;

    @GetMapping("/me/groups")
    public ResponseEntity<ApiPaginateResponse<List<TeacherGroupResponse>>> getMyGroups(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "status", required = false) GroupStatus status,
            @RequestParam(value = "active", required = false) Boolean active,
            @RequestParam(value = "courseId", required = false) UUID courseId,
            @AuthenticationPrincipal CustomUserDetails details
    ) {
        return teacherProfileService.getMyGroups(details, page, size, search, status, active, courseId);
    }

    @GetMapping("/me/groups/{groupId}/students")
    public ResponseEntity<ApiResponse<TeacherGroupStudentsResponse>> getMyGroupStudents(
            @PathVariable UUID groupId,
            @AuthenticationPrincipal CustomUserDetails details
    ) {
        return teacherProfileService.getMyGroupStudents(details, groupId);
    }
}
