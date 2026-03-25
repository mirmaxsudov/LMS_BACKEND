package uz.mirmaxsudov.lmsbackend.service.base.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import uz.mirmaxsudov.lmsbackend.model.request.auth.AuthMeRequest;
import uz.mirmaxsudov.lmsbackend.model.response.ApiResponse;
import uz.mirmaxsudov.lmsbackend.model.response.auth.JwtResponse;
import uz.mirmaxsudov.lmsbackend.model.request.auth.LoginRequest;
import uz.mirmaxsudov.lmsbackend.model.response.auth.AuthMe;
import uz.mirmaxsudov.lmsbackend.security.service.CustomUserDetails;

public interface AuthService {
    ResponseEntity<ApiResponse<JwtResponse>> login(LoginRequest request, HttpServletRequest servletRequest);

    ResponseEntity<ApiResponse<Void>> logout(HttpServletResponse response);

    ResponseEntity<ApiResponse<AuthMe>> getMe(CustomUserDetails details);

    ResponseEntity<ApiResponse<AuthMe>> patchMe(
            AuthMeRequest request,
            MultipartFile profileImage,
            MultipartFile profileBackgroundImage,
            CustomUserDetails details
    );
}
