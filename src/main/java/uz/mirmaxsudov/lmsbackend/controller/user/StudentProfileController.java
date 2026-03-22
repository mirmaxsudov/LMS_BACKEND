package uz.mirmaxsudov.lmsbackend.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.mirmaxsudov.lmsbackend.common.util.APIUtil;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.StudentStatus;
import uz.mirmaxsudov.lmsbackend.model.response.ApiPaginateResponse;
import uz.mirmaxsudov.lmsbackend.model.response.user.user.StudentProfileResponse;
import uz.mirmaxsudov.lmsbackend.service.base.user.StudentProfileService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(APIUtil.API_BASE_URL + "student")
public class StudentProfileController {
    private final StudentProfileService studentProfileService;

    @GetMapping
    public ResponseEntity<ApiPaginateResponse<List<StudentProfileResponse>>> getStudentProfilePaginateResponse(
            @RequestParam(value = "page", defaultValue = "1", required = false) int page,
            @RequestParam(value = "size", defaultValue = "10", required = false) int size,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "status", required = false) StudentStatus status
    ) {
        return studentProfileService.getStudentProfilePaginateResponse(page, size, search, status);
    }
}
