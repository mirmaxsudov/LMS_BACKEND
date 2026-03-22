package uz.mirmaxsudov.lmsbackend.controller.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import uz.mirmaxsudov.lmsbackend.annotations.OpenAuth;
import uz.mirmaxsudov.lmsbackend.config.RateLimit;
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
    @RateLimit(capacity = 10, refill = 10, duration = 60)
    public ResponseEntity<ApiResponse<AuthMe>> getMe(@AuthenticationPrincipal CustomUserDetails details) {
        return authService.getMe(details);
    }
}
