package uz.mirmaxsudov.lmsbackend.controller.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.mirmaxsudov.lmsbackend.common.util.APIUtil;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.TeacherPosition;
import uz.mirmaxsudov.lmsbackend.model.request.user.TeacherProfileRequest;
import uz.mirmaxsudov.lmsbackend.model.response.ApiPaginateResponse;
import uz.mirmaxsudov.lmsbackend.model.response.ApiResponse;
import uz.mirmaxsudov.lmsbackend.model.response.user.user.TeacherProfileResponse;
import uz.mirmaxsudov.lmsbackend.security.service.CustomUserDetails;
import uz.mirmaxsudov.lmsbackend.service.base.user.TeacherProfileService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(APIUtil.API_BASE_URL + "teacher")
public class TeacherProfileController {
    private final TeacherProfileService teacherProfileService;

    @GetMapping
    public ResponseEntity<ApiPaginateResponse<List<TeacherProfileResponse>>> getTeacherProfilePaginateResponse(
            @RequestParam(value = "page", defaultValue = "1", required = false) int page,
            @RequestParam(value = "size", defaultValue = "10", required = false) int size,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "position", required = false) TeacherPosition position
    ) {
        return teacherProfileService.getTeacherProfilePaginateResponse(page, size, search, position);
    }

    @PostMapping(consumes = "multipart/form-data", produces = "application/json")
    public ResponseEntity<ApiResponse<TeacherProfileResponse>> postTeacherProfile(
            @ModelAttribute @Valid TeacherProfileRequest request,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage,
            @RequestPart(value = "profileBackgroundAttachment", required = false) MultipartFile profileBackgroundAttachment,
            @AuthenticationPrincipal CustomUserDetails details
    ) {
        System.out.println("request: " + request);
        System.out.println(profileImage);
        System.out.println(profileBackgroundAttachment);
        return teacherProfileService.postTeacherProfile(request, profileImage, profileBackgroundAttachment, details);
    }
}