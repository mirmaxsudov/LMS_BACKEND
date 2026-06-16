package uz.mirmaxsudov.lmsbackend.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uz.mirmaxsudov.lmsbackend.common.util.APIUtil;
import uz.mirmaxsudov.lmsbackend.model.response.ApiResponse;
import uz.mirmaxsudov.lmsbackend.model.response.lms.StudentWeekScheduleResponse;
import uz.mirmaxsudov.lmsbackend.security.service.CustomUserDetails;
import uz.mirmaxsudov.lmsbackend.service.base.user.StudentProfileService;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping(APIUtil.API_BASE_URL + "students")
public class StudentScheduleController {
    private final StudentProfileService studentProfileService;

    @GetMapping("/me/schedule")
    public ResponseEntity<ApiResponse<StudentWeekScheduleResponse>> getMyWeekSchedule(
            @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @AuthenticationPrincipal CustomUserDetails details
    ) {
        return studentProfileService.getMyWeekSchedule(details, from, to);
    }
}
