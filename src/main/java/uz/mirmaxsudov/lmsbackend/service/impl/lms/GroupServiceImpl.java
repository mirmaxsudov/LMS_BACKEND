package uz.mirmaxsudov.lmsbackend.service.impl.lms;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import uz.mirmaxsudov.lmsbackend.common.filter.PageableBuilder;
import uz.mirmaxsudov.lmsbackend.common.util.lms.LmsAccessControl;
import uz.mirmaxsudov.lmsbackend.common.util.lms.LmsResponseMapper;
import uz.mirmaxsudov.lmsbackend.exceptions.CustomBadRequestException;
import uz.mirmaxsudov.lmsbackend.exceptions.CustomNotFoundException;
import uz.mirmaxsudov.lmsbackend.model.entity.auth.User;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.Course;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.LmsGroup;
import uz.mirmaxsudov.lmsbackend.model.request.lms.GroupCreateRequest;
import uz.mirmaxsudov.lmsbackend.model.request.lms.GroupUpdateRequest;
import uz.mirmaxsudov.lmsbackend.model.response.ApiPaginateResponse;
import uz.mirmaxsudov.lmsbackend.model.response.ApiResponse;
import uz.mirmaxsudov.lmsbackend.model.response.lms.GroupResponse;
import uz.mirmaxsudov.lmsbackend.repository.UserRepository;
import uz.mirmaxsudov.lmsbackend.repository.lms.CourseRepository;
import uz.mirmaxsudov.lmsbackend.repository.lms.LmsGroupRepository;
import uz.mirmaxsudov.lmsbackend.repository.lms.specification.GroupSpecification;
import uz.mirmaxsudov.lmsbackend.repository.lms.specification.dto.GroupFilter;
import uz.mirmaxsudov.lmsbackend.security.service.CustomUserDetails;
import uz.mirmaxsudov.lmsbackend.service.base.lms.GroupService;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {
    private final LmsGroupRepository groupRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    @Override
    public ResponseEntity<ApiResponse<GroupResponse>> create(GroupCreateRequest request, CustomUserDetails details) {
        User currentUser = details.user();
        LmsAccessControl.requireWriteRole(currentUser);

        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new CustomNotFoundException("Course not found"));

        User teacher = userRepository.findById(request.getTeacherId())
                .orElseThrow(() -> new CustomNotFoundException("Teacher not found"));

        validateTeacherAssignment(currentUser, teacher, course);

        if (request.getCapacity() != null && request.getCapacity() < 1) {
            throw new CustomBadRequestException("Group capacity must be at least 1");
        }

        LmsGroup group = LmsGroup.builder()
                .name(request.getName())
                .course(course)
                .teacher(teacher)
                .capacity(request.getCapacity())
                .isActive(request.getActive() == null || request.getActive())
                .build();

        LmsGroup saved = groupRepository.save(group);

        return ResponseEntity.ok(ApiResponse.<GroupResponse>builder()
                .success(true)
                .message("Group created successfully")
                .data(LmsResponseMapper.toGroupResponse(saved))
                .build());
    }

    @Override
    public ResponseEntity<ApiResponse<GroupResponse>> getById(UUID id, CustomUserDetails details) {
        User currentUser = details.user();
        LmsAccessControl.requireReadRole(currentUser);

        LmsGroup group = findVisibleById(id, currentUser);

        return ResponseEntity.ok(ApiResponse.<GroupResponse>builder()
                .success(true)
                .message("Group fetched successfully")
                .data(LmsResponseMapper.toGroupResponse(group))
                .build());
    }

    @Override
    public ResponseEntity<ApiResponse<GroupResponse>> update(UUID id, GroupUpdateRequest request, CustomUserDetails details) {
        User currentUser = details.user();
        LmsAccessControl.requireWriteRole(currentUser);

        LmsGroup group = findWritableById(id, currentUser);

        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new CustomNotFoundException("Course not found"));

        User teacher = userRepository.findById(request.getTeacherId())
                .orElseThrow(() -> new CustomNotFoundException("Teacher not found"));

        validateTeacherAssignment(currentUser, teacher, course);

        if (request.getCapacity() != null && request.getCapacity() < 1) {
            throw new CustomBadRequestException("Group capacity must be at least 1");
        }

        group.setName(request.getName());
        group.setCourse(course);
        group.setTeacher(teacher);
        group.setCapacity(request.getCapacity());
        group.setActive(request.getActive());

        LmsGroup updated = groupRepository.save(group);

        return ResponseEntity.ok(ApiResponse.<GroupResponse>builder()
                .success(true)
                .message("Group updated successfully")
                .data(LmsResponseMapper.toGroupResponse(updated))
                .build());
    }

    @Override
    public ResponseEntity<ApiResponse<Void>> delete(UUID id, CustomUserDetails details) {
        User currentUser = details.user();
        LmsAccessControl.requireWriteRole(currentUser);

        LmsGroup group = findWritableById(id, currentUser);
        groupRepository.delete(group);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Group deleted successfully")
                .build());
    }

    @Override
    public ResponseEntity<ApiPaginateResponse<List<GroupResponse>>> getPaginate(int page, int size, String search, UUID courseId, UUID teacherId, Boolean active, CustomUserDetails details) {
        User currentUser = details.user();
        LmsAccessControl.requireReadRole(currentUser);

        int normalizedPage = Math.max(page - 1, 0);
        int normalizedSize = size <= 0 ? 10 : size;

        Pageable pageable = PageableBuilder.build(normalizedPage, normalizedSize);
        Specification<LmsGroup> specification = GroupSpecification.filter(GroupFilter.builder()
                .search(search)
                .courseId(courseId)
                .teacherId(teacherId)
                .active(active)
                .build());

        specification = applyReadVisibility(specification, currentUser);

        Page<LmsGroup> groups = groupRepository.findAll(specification, pageable);
        List<GroupResponse> results = groups.getContent().stream()
                .map(LmsResponseMapper::toGroupResponse)
                .toList();

        return ResponseEntity.ok(ApiPaginateResponse.<List<GroupResponse>>builder()
                .success(true)
                .message("Groups fetched successfully")
                .results(results)
                .total((int) groups.getTotalElements())
                .page(groups.getNumber() + 1)
                .size(groups.getSize())
                .hasNext(groups.hasNext())
                .build());
    }

    private LmsGroup findVisibleById(UUID id, User currentUser) {
        Specification<LmsGroup> byId = (root, query, cb) -> cb.equal(root.get("id"), id);
        Specification<LmsGroup> specification = applyReadVisibility(byId, currentUser);
        return groupRepository.findOne(specification)
                .orElseThrow(() -> new CustomNotFoundException("Group not found"));
    }

    private LmsGroup findWritableById(UUID id, User currentUser) {
        if (LmsAccessControl.isAdmin(currentUser)) {
            return groupRepository.findById(id)
                    .orElseThrow(() -> new CustomNotFoundException("Group not found"));
        }

        if (LmsAccessControl.isTeacher(currentUser)) {
            return groupRepository.findByIdAndTeacherId(id, currentUser.getId())
                    .orElseThrow(() -> new CustomNotFoundException("Group not found"));
        }

        throw new AccessDeniedException("Only admin or teacher can modify group");
    }

    private Specification<LmsGroup> applyReadVisibility(Specification<LmsGroup> specification, User currentUser) {
        Specification<LmsGroup> result = Specification.where(specification);

        if (LmsAccessControl.isAdmin(currentUser)) {
            return result;
        }

        if (LmsAccessControl.isTeacher(currentUser)) {
            return result.and((root, query, cb) -> cb.equal(root.get("teacher").get("id"), currentUser.getId()));
        }

        if (LmsAccessControl.isStudent(currentUser)) {
            return result.and(GroupSpecification.visibleForStudent(currentUser.getId()));
        }

        if (LmsAccessControl.isParent(currentUser)) {
            return result.and(GroupSpecification.visibleForParent(currentUser.getId()));
        }

        throw new AccessDeniedException("You do not have permission to view groups");
    }

    private void validateTeacherAssignment(User currentUser, User teacher, Course course) {
        if (LmsAccessControl.isAdmin(currentUser)) {
            return;
        }

        if (!Objects.equals(currentUser.getId(), teacher.getId())) {
            throw new AccessDeniedException("Teacher can only create or update own groups");
        }

        if (!Objects.equals(course.getTeacher().getId(), currentUser.getId())) {
            throw new AccessDeniedException("Teacher can only manage groups inside own courses");
        }
    }
}
