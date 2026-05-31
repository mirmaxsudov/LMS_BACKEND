package uz.mirmaxsudov.lmsbackend.controller.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.mirmaxsudov.lmsbackend.annotations.OpenAuth;
import uz.mirmaxsudov.lmsbackend.model.request.auth.AuthMeRequest;
import uz.mirmaxsudov.lmsbackend.model.request.auth.LoginRequest;
import uz.mirmaxsudov.lmsbackend.model.response.ApiResponse;
import uz.mirmaxsudov.lmsbackend.model.response.auth.AuthMe;
import uz.mirmaxsudov.lmsbackend.model.response.auth.JwtResponse;
import uz.mirmaxsudov.lmsbackend.security.service.CustomUserDetails;
import uz.mirmaxsudov.lmsbackend.service.base.auth.AuthService;
import uz.mirmaxsudov.lmsbackend.common.util.APIUtil;

@RestController
@RequiredArgsConstructor
@RequestMapping(APIUtil.API_BASE_URL + "auth")
public class AuthController {
    private final AuthService authService;

    @OpenAuth
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<JwtResponse>> login(
            @RequestBody @Valid LoginRequest request,
            HttpServletRequest servletRequest
    ) {
        return authService.login(request, servletRequest);
    }

    @OpenAuth
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletResponse response) {
        return authService.logout(response);
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<AuthMe>> getMe(@AuthenticationPrincipal CustomUserDetails details) {
        return authService.getMe(details);
    }

    @PatchMapping(value = "/me", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<AuthMe>> patchMeJson(
            @RequestBody @Valid AuthMeRequest request,
            @AuthenticationPrincipal CustomUserDetails details
    ) {
        return authService.patchMe(request, null, null, details);
    }

    @PatchMapping(value = "/me", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<AuthMe>> patchMeMultipart(
            @RequestPart(value = "request", required = false) @Valid AuthMeRequest request,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage,
            @RequestPart(value = "profileBackgroundImage", required = false) MultipartFile profileBackgroundImage,
            @AuthenticationPrincipal CustomUserDetails details) {
        return authService.patchMe(request, profileImage, profileBackgroundImage, details);
    }
}
