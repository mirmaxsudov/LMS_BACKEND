package uz.mirmaxsudov.lmsbackend.service.impl.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uz.mirmaxsudov.lmsbackend.common.filter.PageableBuilder;
import uz.mirmaxsudov.lmsbackend.common.util.mappers.AuthMeMapper;
import uz.mirmaxsudov.lmsbackend.exceptions.CustomNotFoundException;
import uz.mirmaxsudov.lmsbackend.model.entity.auth.User;
import uz.mirmaxsudov.lmsbackend.model.entity.user.TeacherProfile;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.TeacherPosition;
import uz.mirmaxsudov.lmsbackend.model.request.user.TeacherProfileRequest;
import uz.mirmaxsudov.lmsbackend.model.response.ApiPaginateResponse;
import uz.mirmaxsudov.lmsbackend.model.response.ApiResponse;
import uz.mirmaxsudov.lmsbackend.model.response.user.user.TeacherProfileResponse;
import uz.mirmaxsudov.lmsbackend.repository.user.TeacherProfileRepository;
import uz.mirmaxsudov.lmsbackend.repository.user.specification.TeacherProfileSpecification;
import uz.mirmaxsudov.lmsbackend.repository.user.specification.dto.TeacherProfileFilter;
import uz.mirmaxsudov.lmsbackend.security.service.CustomUserDetails;
import uz.mirmaxsudov.lmsbackend.service.base.UserService;
import uz.mirmaxsudov.lmsbackend.service.base.user.TeacherProfileService;
import uz.mirmaxsudov.lmsbackend.service.impl.BaseCRUDServiceImpl;

import java.util.List;
import java.util.Optional;

@Service
public class TeacherProfileServiceImpl extends BaseCRUDServiceImpl<TeacherProfile, TeacherProfileRepository> implements TeacherProfileService {
    private final UserService userService;

    public TeacherProfileServiceImpl(TeacherProfileRepository repository, UserService userService) {
        super(repository);
        this.userService = userService;
    }

    @Override
    public ResponseEntity<ApiPaginateResponse<List<TeacherProfileResponse>>> getTeacherProfilePaginateResponse(int page, int size, String search, TeacherPosition position) {
        int normalizedPage = Math.max(page - 1, 0);
        int normalizedSize = size <= 0 ? 10 : size;
        Pageable pageable = PageableBuilder.build(normalizedPage, normalizedSize);
        Specification<TeacherProfile> filter = TeacherProfileSpecification.filter(TeacherProfileFilter.builder()
                .search(search)
                .position(position)
                .build());

        Page<TeacherProfile> teacherProfiles = repository.findAll(filter, pageable);
        List<TeacherProfileResponse> results = teacherProfiles.getContent().stream()
                .map(teacherProfile -> TeacherProfileResponse.builder()
                        .teacherId(teacherProfile.getId())
                        .position(teacherProfile.getPosition())
                        .user(AuthMeMapper.toResponse(teacherProfile.getUser()))
                        .build())
                .toList();

        return ResponseEntity.ok(
                ApiPaginateResponse.<List<TeacherProfileResponse>>builder()
                        .success(true)
                        .message("Teacher profiles fetched successfully")
                        .results(results)
                        .total((int) teacherProfiles.getTotalElements())
                        .page(teacherProfiles.getNumber() + 1)
                        .size(teacherProfiles.getSize())
                        .hasNext(teacherProfiles.hasNext())
                        .build()
        );
    }

    @Override
    public ResponseEntity<ApiResponse<TeacherProfileResponse>> postTeacherProfile(TeacherProfileRequest request, CustomUserDetails details) {
        User user = userService.getById(request.getUserId()).orElseThrow(() -> new CustomNotFoundException("User not found"));

        TeacherProfile newTeacherProfile = TeacherProfile.builder()
                .user(user)
                .position(request.getPosition())
                .build();

        repository.save(newTeacherProfile);
        return ResponseEntity.ok(ApiResponse.<TeacherProfileResponse>builder()
                .success(true)
                .message("Teacher profile created successfully")
                .data(TeacherProfileResponse.builder()
                        .teacherId(newTeacherProfile.getId())
                        .position(newTeacherProfile.getPosition())
                        .user(AuthMeMapper.toResponse(newTeacherProfile.getUser()))
                        .build())
                .build());
    }
}