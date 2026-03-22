package uz.mirmaxsudov.lmsbackend.service.impl.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uz.mirmaxsudov.lmsbackend.common.filter.PageableBuilder;
import uz.mirmaxsudov.lmsbackend.common.util.mappers.AuthMeMapper;
import uz.mirmaxsudov.lmsbackend.model.entity.user.StudentProfile;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.StudentStatus;
import uz.mirmaxsudov.lmsbackend.model.response.ApiPaginateResponse;
import uz.mirmaxsudov.lmsbackend.model.response.user.user.StudentProfileResponse;
import uz.mirmaxsudov.lmsbackend.repository.user.StudentProfileRepository;
import uz.mirmaxsudov.lmsbackend.repository.user.specification.StudentProfileSpecification;
import uz.mirmaxsudov.lmsbackend.repository.user.specification.dto.StudentProfileFilter;
import uz.mirmaxsudov.lmsbackend.service.base.user.StudentProfileService;
import uz.mirmaxsudov.lmsbackend.service.impl.BaseCRUDServiceImpl;

import java.util.List;

@Service
public class StudentProfileServiceImpl extends BaseCRUDServiceImpl<StudentProfile, StudentProfileRepository> implements StudentProfileService {
    public StudentProfileServiceImpl(StudentProfileRepository repository) {
        super(repository);
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
}

