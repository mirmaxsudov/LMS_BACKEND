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
import uz.mirmaxsudov.lmsbackend.model.request.user.ParentProfileRequest;
import uz.mirmaxsudov.lmsbackend.model.response.ApiPaginateResponse;
import uz.mirmaxsudov.lmsbackend.model.response.ApiResponse;
import uz.mirmaxsudov.lmsbackend.model.response.user.user.ParentProfileResponse;
import uz.mirmaxsudov.lmsbackend.security.service.CustomUserDetails;
import uz.mirmaxsudov.lmsbackend.service.base.user.ParentProfileService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(APIUtil.API_BASE_URL + "parent")
public class ParentProfileController {
    private final ParentProfileService parentProfileService;

    @GetMapping
    public ResponseEntity<ApiPaginateResponse<List<ParentProfileResponse>>> getParentProfilePaginateResponse(
            @RequestParam(value = "page", defaultValue = "1", required = false) int page,
            @RequestParam(value = "size", defaultValue = "10", required = false) int size,
            @RequestParam(value = "search", required = false) String search
    ) {
        return parentProfileService.getParentProfilePaginateResponse(page, size, search);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ParentProfileResponse>> postParentProfile(
            @ModelAttribute @Valid ParentProfileRequest request,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage,
            @RequestPart(value = "profileBackgroundAttachment", required = false) MultipartFile profileBackgroundAttachment,
            @AuthenticationPrincipal CustomUserDetails details
    ) {
        return parentProfileService.postParentProfile(request, profileImage, profileBackgroundAttachment, details);
    }
}
