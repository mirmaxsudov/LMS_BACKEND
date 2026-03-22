package uz.mirmaxsudov.lmsbackend.service.impl.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uz.mirmaxsudov.lmsbackend.common.util.mappers.AuthMeMapper;
import uz.mirmaxsudov.lmsbackend.common.filter.PageableBuilder;
import uz.mirmaxsudov.lmsbackend.model.entity.user.TeacherProfile;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.TeacherPosition;
import uz.mirmaxsudov.lmsbackend.model.response.ApiPaginateResponse;
import uz.mirmaxsudov.lmsbackend.model.response.user.user.TeacherProfileResponse;
import uz.mirmaxsudov.lmsbackend.repository.user.TeacherProfileRepository;
import uz.mirmaxsudov.lmsbackend.repository.user.specification.TeacherProfileSpecification;
import uz.mirmaxsudov.lmsbackend.repository.user.specification.dto.TeacherProfileFilter;
import uz.mirmaxsudov.lmsbackend.service.base.user.TeacherProfileService;
import uz.mirmaxsudov.lmsbackend.service.impl.BaseCRUDServiceImpl;

import java.util.List;

@Service
public class TeacherProfileServiceImpl extends BaseCRUDServiceImpl<TeacherProfile, TeacherProfileRepository> implements TeacherProfileService {
    public TeacherProfileServiceImpl(TeacherProfileRepository repository) {
        super(repository);
    }

    @Override
    public ResponseEntity<ApiPaginateResponse<List<TeacherProfileResponse>>> getTeacherProfilePaginateResponse(int page, int size, String search, TeacherPosition position) {
        Pageable pageable = PageableBuilder.build(page, size);
        Specification<TeacherProfile> filter = TeacherProfileSpecification.filter(TeacherProfileFilter.builder()
                .search(search)
                .position(position)
                .build());

        Page<TeacherProfile> teacherProfiles = repository.findAll(filter, pageable);
        List<TeacherProfileResponse> results = teacherProfiles.getContent().stream()
                .map(teacherProfile -> TeacherProfileResponse.builder()
                        .position(teacherProfile.getPosition())
                        .baseData(AuthMeMapper.toResponse(teacherProfile.getUser()))
                        .build())
                .toList();

        return ResponseEntity.ok(
                ApiPaginateResponse.<List<TeacherProfileResponse>>builder()
                        .success(true)
                        .message("Teacher profiles fetched successfully")
                        .results(results)
                        .total((int) teacherProfiles.getTotalElements())
                        .page(teacherProfiles.getNumber())
                        .size(teacherProfiles.getSize())
                        .hasNext(teacherProfiles.hasNext())
                        .build()
        );
    }
}
