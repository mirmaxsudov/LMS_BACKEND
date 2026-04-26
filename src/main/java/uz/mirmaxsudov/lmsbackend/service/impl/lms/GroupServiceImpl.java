package uz.mirmaxsudov.lmsbackend.service.impl.lms;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uz.mirmaxsudov.lmsbackend.common.filter.PageableBuilder;
import uz.mirmaxsudov.lmsbackend.common.util.mappers.GroupMapper;
import uz.mirmaxsudov.lmsbackend.common.util.mappers.TeacherMapper;
import uz.mirmaxsudov.lmsbackend.exceptions.CustomBadRequestException;
import uz.mirmaxsudov.lmsbackend.exceptions.CustomConflictException;
import uz.mirmaxsudov.lmsbackend.exceptions.CustomNotFoundException;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.Course;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.Group;
import uz.mirmaxsudov.lmsbackend.model.entity.user.TeacherProfile;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.GroupStatus;
import uz.mirmaxsudov.lmsbackend.model.request.lms.GroupCreateRequest;
import uz.mirmaxsudov.lmsbackend.model.request.lms.GroupUpdateRequest;
import uz.mirmaxsudov.lmsbackend.model.response.ApiPaginateResponse;
import uz.mirmaxsudov.lmsbackend.model.response.ApiResponse;
import uz.mirmaxsudov.lmsbackend.model.response.lms.GroupResponse;
import uz.mirmaxsudov.lmsbackend.repository.lms.course.CourseRepository;
import uz.mirmaxsudov.lmsbackend.repository.lms.group.GroupFilter;
import uz.mirmaxsudov.lmsbackend.repository.lms.group.GroupRepository;
import uz.mirmaxsudov.lmsbackend.repository.lms.group.GroupSpecification;
import uz.mirmaxsudov.lmsbackend.repository.user.TeacherProfileRepository;
import uz.mirmaxsudov.lmsbackend.service.base.lms.GroupService;
import uz.mirmaxsudov.lmsbackend.service.impl.BaseCRUDServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class GroupServiceImpl extends BaseCRUDServiceImpl<Group, GroupRepository> implements GroupService {
    private final CourseRepository courseRepository;
    private final TeacherProfileRepository teacherProfileRepository;

    public GroupServiceImpl(
            GroupRepository repository,
            CourseRepository courseRepository,
            TeacherProfileRepository teacherProfileRepository
    ) {
        super(repository);
        this.courseRepository = courseRepository;
        this.teacherProfileRepository = teacherProfileRepository;
    }

    @Override
    public ResponseEntity<ApiPaginateResponse<List<GroupResponse>>> getAll(
            int page,
            int size,
            String search,
            UUID courseId,
            UUID teacherId,
            GroupStatus status,
            Boolean active,
            Integer minCapacity,
            Integer maxCapacity
    ) {
        int normalizedPage = Math.max(page - 1, 0);
        int normalizedSize = size <= 0 ? 10 : size;
        validateCapacityRange(minCapacity, maxCapacity);

        Pageable pageable = PageableBuilder.build(normalizedPage, normalizedSize);

        Specification<Group> filter = GroupSpecification.filter(GroupFilter.builder()
                .search(search)
                .courseId(courseId)
                .teacherId(teacherId)
                .status(status)
                .active(active)
                .minCapacity(minCapacity)
                .maxCapacity(maxCapacity)
                .build());

        Page<Group> groups = repository.findAll(filter, pageable);
        List<GroupResponse> results = groups.getContent().stream()
                .map(GroupMapper::toResponse)
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

    @Override
    public ResponseEntity<ApiResponse<GroupResponse>> getByIdResponse(UUID id) {
        Group group = findActiveGroup(id);

        return ResponseEntity.ok(ApiResponse.<GroupResponse>builder()
                .success(true)
                .message("Group fetched successfully")
                .data(GroupMapper.toResponse(group))
                .build());
    }

    @Override
    public ResponseEntity<ApiResponse<GroupResponse>> createGroup(GroupCreateRequest request) {
        String normalizedName = normalizeName(request.getName());
        Course course = findCourse(request.getCourseId());
        TeacherProfile teacher = findTeacher(request.getTeacherId());

        validateDuplicateName(normalizedName, course.getId(), null);
        validateCapacity(request.getCapacity(), 0);

        Group group = Group.builder()
                .groupName(normalizedName)
                .course(course)
                .teacher(teacher)
                .status(toStatus(request.getActive()))
                .capacity(request.getCapacity())
                .build();

        Group savedGroup = repository.save(group);

        return ResponseEntity.ok(ApiResponse.<GroupResponse>builder()
                .success(true)
                .message("Group created successfully")
                .data(GroupMapper.toResponse(savedGroup))
                .build());
    }

    @Override
    public ResponseEntity<ApiResponse<GroupResponse>> updateGroup(UUID id, GroupUpdateRequest request) {
        Group existingGroup = findActiveGroup(id);

        String normalizedName = normalizeName(request.getName());
        Course course = findCourse(request.getCourseId());
        TeacherProfile teacher = findTeacher(request.getTeacherId());
        int currentStudentsCount = existingGroup.getStudents() == null ? 0 : existingGroup.getStudents().size();

        validateDuplicateName(normalizedName, course.getId(), existingGroup.getId());
        validateCapacity(request.getCapacity(), currentStudentsCount);

        existingGroup.setGroupName(normalizedName);
        existingGroup.setCourse(course);
        existingGroup.setTeacher(teacher);
        existingGroup.setStatus(toStatus(request.getActive()));
        existingGroup.setCapacity(request.getCapacity());
        existingGroup.setStatus(request.getStatus());

        Group updatedGroup = repository.save(existingGroup);

        return ResponseEntity.ok(ApiResponse.<GroupResponse>builder()
                .success(true)
                .message("Group updated successfully")
                .data(GroupMapper.toResponse(updatedGroup))
                .build());
    }

    @Override
    public ResponseEntity<ApiResponse<Void>> deleteGroup(UUID id) {
        Group existingGroup = findActiveGroup(id);

        existingGroup.setDeleted(true);
        existingGroup.setDeletedAt(LocalDateTime.now());
        repository.save(existingGroup);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Group deleted successfully")
                .build());
    }

    private Course findCourse(UUID courseId) {
        return courseRepository.findByIdAndDeletedFalse(courseId)
                .orElseThrow(() -> new CustomNotFoundException("Course not found with id: " + courseId));
    }

    private TeacherProfile findTeacher(UUID teacherId) {
        return teacherProfileRepository.findByIdAndDeletedFalse(teacherId)
                .orElseThrow(() -> new CustomNotFoundException("Teacher profile not found with id: " + teacherId));
    }

    private String normalizeName(String name) {
        if (name == null)
            throw new CustomBadRequestException("Group name is required");

        String trimmed = name.trim();
        if (trimmed.isBlank())
            throw new CustomBadRequestException("Group name is required");

        return trimmed;
    }

    private GroupStatus toStatus(Boolean active) {
        return Boolean.FALSE.equals(active) ? GroupStatus.CANCELLED : GroupStatus.ACTIVE;
    }

    private void validateDuplicateName(String groupName, UUID courseId, UUID excludedGroupId) {
        boolean exists = excludedGroupId == null
                ? repository.existsByGroupNameIgnoreCaseAndCourseIdAndDeletedFalse(groupName, courseId)
                : repository.existsByGroupNameIgnoreCaseAndCourseIdAndIdNotAndDeletedFalse(
                groupName,
                courseId,
                excludedGroupId
        );

        if (exists)
            throw new CustomConflictException("Group with this name already exists in the selected course");
    }

    private Group findActiveGroup(UUID id) {
        return repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new CustomNotFoundException("Group not found with id: " + id));
    }

    private void validateCapacity(Integer capacity, int currentStudentsCount) {
        if (capacity == null || capacity <= 0)
            throw new CustomBadRequestException("Capacity must be greater than 0");

        if (capacity < currentStudentsCount)
            throw new CustomBadRequestException(
                    "Capacity cannot be less than current students count: " + currentStudentsCount
            );
    }

    private void validateCapacityRange(Integer minCapacity, Integer maxCapacity) {
        if (minCapacity != null && minCapacity <= 0)
            throw new CustomBadRequestException("minCapacity must be greater than 0");

        if (maxCapacity != null && maxCapacity <= 0)
            throw new CustomBadRequestException("maxCapacity must be greater than 0");

        if (minCapacity != null && maxCapacity != null && minCapacity > maxCapacity)
            throw new CustomBadRequestException("minCapacity must be less than or equal to maxCapacity");
    }
}
