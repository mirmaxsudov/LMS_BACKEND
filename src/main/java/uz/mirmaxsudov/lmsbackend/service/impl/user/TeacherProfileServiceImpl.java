package uz.mirmaxsudov.lmsbackend.service.impl.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import uz.mirmaxsudov.lmsbackend.common.filter.PageableBuilder;
import uz.mirmaxsudov.lmsbackend.common.util.mappers.TeacherMapper;
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
                .map(TeacherMapper::toResponse)
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
    @Transactional
    public ResponseEntity<ApiResponse<TeacherProfileResponse>> postTeacherProfile(
            TeacherProfileRequest request,
            MultipartFile profileImage,
            MultipartFile profileBackgroundAttachment,
            CustomUserDetails details
    ) {
        User user = userService.createUserEntity(request, profileImage, profileBackgroundAttachment, details);

        TeacherProfile newTeacherProfile = TeacherProfile.builder()
                .user(user)
                .position(request.getPosition())
                .build();

        repository.save(newTeacherProfile);
        return ResponseEntity.ok(ApiResponse.<TeacherProfileResponse>builder()
                .success(true)
                .message("Teacher profile created successfully")
                .data(TeacherMapper.toResponse(newTeacherProfile))
                .build());
    }
}
