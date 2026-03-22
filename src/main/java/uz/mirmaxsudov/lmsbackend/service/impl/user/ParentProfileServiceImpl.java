package uz.mirmaxsudov.lmsbackend.service.impl.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uz.mirmaxsudov.lmsbackend.common.filter.PageableBuilder;
import uz.mirmaxsudov.lmsbackend.common.util.mappers.AuthMeMapper;
import uz.mirmaxsudov.lmsbackend.model.entity.user.ParentProfile;
import uz.mirmaxsudov.lmsbackend.model.response.ApiPaginateResponse;
import uz.mirmaxsudov.lmsbackend.model.response.user.user.ParentProfileResponse;
import uz.mirmaxsudov.lmsbackend.repository.user.ParentProfileRepository;
import uz.mirmaxsudov.lmsbackend.repository.user.specification.ParentProfileSpecification;
import uz.mirmaxsudov.lmsbackend.repository.user.specification.dto.ParentProfileFilter;
import uz.mirmaxsudov.lmsbackend.service.base.user.ParentProfileService;
import uz.mirmaxsudov.lmsbackend.service.impl.BaseCRUDServiceImpl;

import java.util.List;

@Service
public class ParentProfileServiceImpl extends BaseCRUDServiceImpl<ParentProfile, ParentProfileRepository> implements ParentProfileService {
    public ParentProfileServiceImpl(ParentProfileRepository repository) {
        super(repository);
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
                .map(parentProfile -> ParentProfileResponse.builder()
                        .baseData(AuthMeMapper.toResponse(parentProfile.getUser()))
                        .studentsCount(parentProfile.getStudents() == null ? 0 : parentProfile.getStudents().size())
                        .build())
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
}

