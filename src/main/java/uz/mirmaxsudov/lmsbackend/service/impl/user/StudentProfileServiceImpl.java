package uz.mirmaxsudov.lmsbackend.service.impl.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import uz.mirmaxsudov.lmsbackend.common.filter.PageableBuilder;
import uz.mirmaxsudov.lmsbackend.common.util.mappers.StudentMapper;
import uz.mirmaxsudov.lmsbackend.model.entity.auth.User;
import uz.mirmaxsudov.lmsbackend.model.entity.user.StudentProfile;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.StudentStatus;
import uz.mirmaxsudov.lmsbackend.model.request.user.StudentProfileRequest;
import uz.mirmaxsudov.lmsbackend.model.response.ApiPaginateResponse;
import uz.mirmaxsudov.lmsbackend.model.response.ApiResponse;
import uz.mirmaxsudov.lmsbackend.model.response.user.user.StudentProfileResponse;
import uz.mirmaxsudov.lmsbackend.repository.user.StudentProfileRepository;
import uz.mirmaxsudov.lmsbackend.repository.user.specification.StudentProfileSpecification;
import uz.mirmaxsudov.lmsbackend.repository.user.specification.dto.StudentProfileFilter;
import uz.mirmaxsudov.lmsbackend.security.service.CustomUserDetails;
import uz.mirmaxsudov.lmsbackend.service.base.UserService;
import uz.mirmaxsudov.lmsbackend.service.base.user.StudentProfileService;
import uz.mirmaxsudov.lmsbackend.service.impl.BaseCRUDServiceImpl;

import java.util.List;
import java.util.UUID;

@Service
public class StudentProfileServiceImpl extends BaseCRUDServiceImpl<StudentProfile, StudentProfileRepository> implements StudentProfileService {
    private final UserService userService;

    public StudentProfileServiceImpl(StudentProfileRepository repository, UserService userService) {
        super(repository);
        this.userService = userService;
    }

    @Override
    public ResponseEntity<ApiPaginateResponse<List<StudentProfileResponse>>> getStudentProfilePaginateResponse(int page, int size, String search, StudentStatus status) {
        int normalizedPage = Math.max(page - 1, 0);
        int normalizedSize = size <= 0 ? 10 : size;
        Pageable pageable = PageableBuilder.build(normalizedPage, normalizedSize);

        Specification<StudentProfile> filter = StudentProfileSpecification.filter(StudentProfileFilter.builder()
                .search(search)
                .status(status)
                .build());

        Page<StudentProfile> studentProfiles = repository.findAll(filter, pageable);
        List<StudentProfileResponse> results = studentProfiles.getContent().stream()
                .map(StudentMapper::toResponse)
                .toList();

        return ResponseEntity.ok(
                ApiPaginateResponse.<List<StudentProfileResponse>>builder()
                        .success(true)
                        .message("Student profiles fetched successfully")
                        .results(results)
                        .total((int) studentProfiles.getTotalElements())
                        .page(studentProfiles.getNumber() + 1)
                        .size(studentProfiles.getSize())
                        .hasNext(studentProfiles.hasNext())
                        .build()
        );
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponse<StudentProfileResponse>> postStudentProfile(
            StudentProfileRequest request,
            MultipartFile profileImage,
            MultipartFile profileBackgroundAttachment,
            CustomUserDetails details
    ) {
        User user = userService.createUserEntity(request, profileImage, profileBackgroundAttachment, details);
        StudentProfile profile = StudentProfile.builder()
                .user(user)
                .studentId(UUID.randomUUID())
                .status(request.getStudentStatus())
                .build();

        repository.save(profile);

        return ResponseEntity.ok(ApiResponse.<StudentProfileResponse>builder()
                .success(true)
                .message("Student profile created successfully")
                .data(StudentMapper.toResponse(profile))
                .build());
    }
}
