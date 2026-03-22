package uz.mirmaxsudov.lmsbackend.service.impl.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uz.mirmaxsudov.lmsbackend.common.filter.PageableBuilder;
import uz.mirmaxsudov.lmsbackend.common.util.mappers.AuthMeMapper;
import uz.mirmaxsudov.lmsbackend.exceptions.CustomConflictException;
import uz.mirmaxsudov.lmsbackend.exceptions.CustomNotFoundException;
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
                .map(studentProfile -> StudentProfileResponse.builder()
                        .baseData(AuthMeMapper.toResponse(studentProfile.getUser()))
                        .studentId(studentProfile.getStudentId())
                        .status(studentProfile.getStatus())
                        .build())
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
    public ResponseEntity<ApiResponse<StudentProfileResponse>> postStudentProfile(StudentProfileRequest request, CustomUserDetails details) {
        User user = userService.getById(request.getUserId())
                .orElseThrow(() -> new CustomNotFoundException("User not found"));

        if (repository.findByUserId(request.getUserId()).isPresent())
            throw new CustomConflictException("Student profile already exists for this user");

        StudentProfile profile = StudentProfile.builder()
                .user(user)
                .studentId(request.getStudentId())
                .status(request.getStatus())
                .build();

        repository.save(profile);

        return ResponseEntity.ok(ApiResponse.<StudentProfileResponse>builder()
                .success(true)
                .message("Student profile created successfully")
                .data(StudentProfileResponse.builder()
                        .baseData(AuthMeMapper.toResponse(profile.getUser()))
                        .studentId(profile.getStudentId())
                        .status(profile.getStatus())
                        .build())
                .build());
    }
}

