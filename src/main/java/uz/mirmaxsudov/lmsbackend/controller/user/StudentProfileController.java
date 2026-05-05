package uz.mirmaxsudov.lmsbackend.controller.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import uz.mirmaxsudov.lmsbackend.common.util.APIUtil;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.StudentStatus;
import uz.mirmaxsudov.lmsbackend.model.request.user.StudentProfileRequest;
import uz.mirmaxsudov.lmsbackend.model.response.ApiPaginateResponse;
import uz.mirmaxsudov.lmsbackend.model.response.ApiResponse;
import uz.mirmaxsudov.lmsbackend.model.response.user.user.StudentProfileResponse;
import uz.mirmaxsudov.lmsbackend.security.service.CustomUserDetails;
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

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<StudentProfileResponse>> postStudentProfile(
            @ModelAttribute @Valid StudentProfileRequest request,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage,
            @RequestPart(value = "profileBackgroundAttachment", required = false) MultipartFile profileBackgroundAttachment,
            @AuthenticationPrincipal CustomUserDetails details
    ) {
        return studentProfileService.postStudentProfile(request, profileImage, profileBackgroundAttachment, details);
    }
}
