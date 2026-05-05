package uz.mirmaxsudov.lmsbackend.service.impl.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import uz.mirmaxsudov.lmsbackend.common.filter.PageableBuilder;
import uz.mirmaxsudov.lmsbackend.common.util.mappers.ParentMapper;
import uz.mirmaxsudov.lmsbackend.exceptions.CustomNotFoundException;
import uz.mirmaxsudov.lmsbackend.model.entity.auth.User;
import uz.mirmaxsudov.lmsbackend.model.entity.user.ParentProfile;
import uz.mirmaxsudov.lmsbackend.model.entity.user.StudentProfile;
import uz.mirmaxsudov.lmsbackend.model.request.user.ParentProfileRequest;
import uz.mirmaxsudov.lmsbackend.model.response.ApiPaginateResponse;
import uz.mirmaxsudov.lmsbackend.model.response.ApiResponse;
import uz.mirmaxsudov.lmsbackend.model.response.user.user.ParentProfileResponse;
import uz.mirmaxsudov.lmsbackend.repository.user.ParentProfileRepository;
import uz.mirmaxsudov.lmsbackend.repository.user.StudentProfileRepository;
import uz.mirmaxsudov.lmsbackend.repository.user.specification.ParentProfileSpecification;
import uz.mirmaxsudov.lmsbackend.repository.user.specification.dto.ParentProfileFilter;
import uz.mirmaxsudov.lmsbackend.security.service.CustomUserDetails;
import uz.mirmaxsudov.lmsbackend.service.base.UserService;
import uz.mirmaxsudov.lmsbackend.service.base.user.ParentProfileService;
import uz.mirmaxsudov.lmsbackend.service.impl.BaseCRUDServiceImpl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class ParentProfileServiceImpl extends BaseCRUDServiceImpl<ParentProfile, ParentProfileRepository> implements ParentProfileService {
    private final UserService userService;
    private final StudentProfileRepository studentProfileRepository;

    public ParentProfileServiceImpl(
            ParentProfileRepository repository,
            UserService userService,
            StudentProfileRepository studentProfileRepository
    ) {
        super(repository);
        this.userService = userService;
        this.studentProfileRepository = studentProfileRepository;
    }

    @Override
    public ResponseEntity<ApiPaginateResponse<List<ParentProfileResponse>>> getParentProfilePaginateResponse(int page, int size, String search) {
        int normalizedPage = Math.max(page - 1, 0);
        int normalizedSize = size <= 0 ? 10 : size;
        Pageable pageable = PageableBuilder.build(normalizedPage, normalizedSize);
        Specification<ParentProfile> filter = ParentProfileSpecification.filter(ParentProfileFilter.builder()
                .search(search)
                .build());

        Page<ParentProfile> parentProfiles = repository.findAll(filter, pageable);
        List<ParentProfileResponse> results = parentProfiles.getContent().stream()
                .map(ParentMapper::toResponse)
                .toList();

        return ResponseEntity.ok(
                ApiPaginateResponse.<List<ParentProfileResponse>>builder()
                        .success(true)
                        .message("Parent profiles fetched successfully")
                        .results(results)
                        .total((int) parentProfiles.getTotalElements())
                        .page(parentProfiles.getNumber() + 1)
                        .size(parentProfiles.getSize())
                        .hasNext(parentProfiles.hasNext())
                        .build()
        );
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponse<ParentProfileResponse>> postParentProfile(
            ParentProfileRequest request,
            MultipartFile profileImage,
            MultipartFile profileBackgroundAttachment,
            CustomUserDetails details
    ) {
        User user = userService.createUserEntity(request, profileImage, profileBackgroundAttachment, details);
        Set<StudentProfile> students = resolveStudents(request.getStudentIds());

        ParentProfile profile = ParentProfile.builder()
                .user(user)
                .students(students)
                .build();

        repository.save(profile);

        return ResponseEntity.ok(ApiResponse.<ParentProfileResponse>builder()
                .success(true)
                .message("Parent profile created successfully")
                .data(ParentMapper.toResponse(profile))
                .build());
    }

    private Set<StudentProfile> resolveStudents(Set<UUID> studentIds) {
        if (studentIds == null || studentIds.isEmpty())
            return new HashSet<>();

        List<StudentProfile> students = studentProfileRepository.findAllById(studentIds);
        if (students.size() != studentIds.size())
            throw new CustomNotFoundException("One or more student profiles not found");

        return new HashSet<>(students);
    }
}
