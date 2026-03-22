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
import uz.mirmaxsudov.lmsbackend.exceptions.CustomConflictException;
import uz.mirmaxsudov.lmsbackend.exceptions.CustomNotFoundException;
import uz.mirmaxsudov.lmsbackend.model.entity.auth.User;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.Enrollment;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.LmsGroup;
import uz.mirmaxsudov.lmsbackend.model.entity.user.StudentProfile;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.EnrollmentStatus;
import uz.mirmaxsudov.lmsbackend.model.request.lms.EnrollmentCreateRequest;
import uz.mirmaxsudov.lmsbackend.model.request.lms.EnrollmentUpdateRequest;
import uz.mirmaxsudov.lmsbackend.model.response.ApiPaginateResponse;
import uz.mirmaxsudov.lmsbackend.model.response.ApiResponse;
import uz.mirmaxsudov.lmsbackend.model.response.lms.EnrollmentResponse;
import uz.mirmaxsudov.lmsbackend.repository.lms.EnrollmentRepository;
import uz.mirmaxsudov.lmsbackend.repository.lms.LmsGroupRepository;
import uz.mirmaxsudov.lmsbackend.repository.lms.specification.EnrollmentSpecification;
import uz.mirmaxsudov.lmsbackend.repository.lms.specification.dto.EnrollmentFilter;
import uz.mirmaxsudov.lmsbackend.repository.user.StudentProfileRepository;
import uz.mirmaxsudov.lmsbackend.security.service.CustomUserDetails;
import uz.mirmaxsudov.lmsbackend.service.base.lms.EnrollmentService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;
    private final LmsGroupRepository groupRepository;
    private final StudentProfileRepository studentProfileRepository;

    @Override
    public ResponseEntity<ApiResponse<EnrollmentResponse>> create(EnrollmentCreateRequest request, CustomUserDetails details) {
        User currentUser = details.user();
        LmsAccessControl.requireWriteRole(currentUser);

        validateCreateRequest(request);

        LmsGroup group = groupRepository.findById(request.getGroupId())
                .orElseThrow(() -> new CustomNotFoundException("Group not found"));

        if (LmsAccessControl.isTeacher(currentUser) && !Objects.equals(group.getTeacher().getId(), currentUser.getId())) {
            throw new AccessDeniedException("Teacher can only enroll students into own groups");
        }

        StudentProfile studentProfile = studentProfileRepository.findById(request.getStudentProfileId())
                .orElseThrow(() -> new CustomNotFoundException("Student profile not found"));

        if (enrollmentRepository.existsByGroupIdAndStudentProfileId(group.getId(), studentProfile.getId())) {
            throw new CustomConflictException("Student already enrolled in this group");
        }

        validateGroupCapacity(group, EnrollmentStatus.ACTIVE, null);

        Enrollment enrollment = Enrollment.builder()
                .group(group)
                .studentProfile(studentProfile)
                .status(EnrollmentStatus.ACTIVE)
                .enrolledAt(LocalDateTime.now())
                .build();

        Enrollment saved = enrollmentRepository.save(enrollment);

        return ResponseEntity.ok(ApiResponse.<EnrollmentResponse>builder()
                .success(true)
                .message("Enrollment created successfully")
                .data(LmsResponseMapper.toEnrollmentResponse(saved))
                .build());
    }

    @Override
    public ResponseEntity<ApiResponse<EnrollmentResponse>> getById(UUID id, CustomUserDetails details) {
        User currentUser = details.user();
        LmsAccessControl.requireReadRole(currentUser);

        Enrollment enrollment = findVisibleById(id, currentUser);

        return ResponseEntity.ok(ApiResponse.<EnrollmentResponse>builder()
                .success(true)
                .message("Enrollment fetched successfully")
                .data(LmsResponseMapper.toEnrollmentResponse(enrollment))
                .build());
    }

    @Override
    public ResponseEntity<ApiResponse<EnrollmentResponse>> update(UUID id, EnrollmentUpdateRequest request, CustomUserDetails details) {
        User currentUser = details.user();
        LmsAccessControl.requireWriteRole(currentUser);

        Enrollment enrollment = findWritableById(id, currentUser);

        LmsGroup group = enrollment.getGroup();
        if (request.getGroupId() != null && !Objects.equals(request.getGroupId(), group.getId())) {
            group = groupRepository.findById(request.getGroupId())
                    .orElseThrow(() -> new CustomNotFoundException("Group not found"));

            if (LmsAccessControl.isTeacher(currentUser) && !Objects.equals(group.getTeacher().getId(), currentUser.getId())) {
                throw new AccessDeniedException("Teacher can only move enrollment to own groups");
            }
        }

        StudentProfile studentProfile = enrollment.getStudentProfile();
        if (request.getStudentProfileId() != null && !Objects.equals(request.getStudentProfileId(), studentProfile.getId())) {
            studentProfile = studentProfileRepository.findById(request.getStudentProfileId())
                    .orElseThrow(() -> new CustomNotFoundException("Student profile not found"));
        }

        boolean pairChanged = !Objects.equals(group.getId(), enrollment.getGroup().getId())
                || !Objects.equals(studentProfile.getId(), enrollment.getStudentProfile().getId());
        if (pairChanged && enrollmentRepository.existsByGroupIdAndStudentProfileId(group.getId(), studentProfile.getId())) {
            throw new CustomConflictException("Student already enrolled in this group");
        }

        validateGroupCapacity(group, request.getStatus(), enrollment);

        enrollment.setGroup(group);
        enrollment.setStudentProfile(studentProfile);
        enrollment.setStatus(request.getStatus());

        Enrollment updated = enrollmentRepository.save(enrollment);

        return ResponseEntity.ok(ApiResponse.<EnrollmentResponse>builder()
                .success(true)
                .message("Enrollment updated successfully")
                .data(LmsResponseMapper.toEnrollmentResponse(updated))
                .build());
    }

    @Override
    public ResponseEntity<ApiResponse<Void>> delete(UUID id, CustomUserDetails details) {
        User currentUser = details.user();
        LmsAccessControl.requireWriteRole(currentUser);

        Enrollment enrollment = findWritableById(id, currentUser);
        enrollmentRepository.delete(enrollment);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Enrollment deleted successfully")
                .build());
    }

    @Override
    public ResponseEntity<ApiPaginateResponse<List<EnrollmentResponse>>> getPaginate(int page, int size, UUID groupId, UUID studentProfileId, EnrollmentStatus status, CustomUserDetails details) {
        User currentUser = details.user();
        LmsAccessControl.requireReadRole(currentUser);

        int normalizedPage = Math.max(page - 1, 0);
        int normalizedSize = size <= 0 ? 10 : size;

        Pageable pageable = PageableBuilder.build(normalizedPage, normalizedSize);
        Specification<Enrollment> specification = EnrollmentSpecification.filter(EnrollmentFilter.builder()
                .groupId(groupId)
                .studentProfileId(studentProfileId)
                .status(status)
                .build());

        specification = applyReadVisibility(specification, currentUser);

        Page<Enrollment> enrollments = enrollmentRepository.findAll(specification, pageable);
        List<EnrollmentResponse> results = enrollments.getContent().stream()
                .map(LmsResponseMapper::toEnrollmentResponse)
                .toList();

        return ResponseEntity.ok(ApiPaginateResponse.<List<EnrollmentResponse>>builder()
                .success(true)
                .message("Enrollments fetched successfully")
                .results(results)
                .total((int) enrollments.getTotalElements())
                .page(enrollments.getNumber() + 1)
                .size(enrollments.getSize())
                .hasNext(enrollments.hasNext())
                .build());
    }

    private Enrollment findVisibleById(UUID id, User currentUser) {
        Specification<Enrollment> byId = (root, query, cb) -> cb.equal(root.get("id"), id);
        Specification<Enrollment> specification = applyReadVisibility(byId, currentUser);
        return enrollmentRepository.findOne(specification)
                .orElseThrow(() -> new CustomNotFoundException("Enrollment not found"));
    }

    private Enrollment findWritableById(UUID id, User currentUser) {
        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new CustomNotFoundException("Enrollment not found"));

        if (LmsAccessControl.isAdmin(currentUser)) {
            return enrollment;
        }

        if (LmsAccessControl.isTeacher(currentUser)
                && Objects.equals(enrollment.getGroup().getTeacher().getId(), currentUser.getId())) {
            return enrollment;
        }

        throw new AccessDeniedException("Only admin or group owner teacher can modify enrollment");
    }

    private Specification<Enrollment> applyReadVisibility(Specification<Enrollment> specification, User currentUser) {
        Specification<Enrollment> result = Specification.where(specification);

        if (LmsAccessControl.isAdmin(currentUser)) {
            return result;
        }

        if (LmsAccessControl.isTeacher(currentUser)) {
            return result.and(EnrollmentSpecification.visibleForTeacher(currentUser.getId()));
        }

        if (LmsAccessControl.isStudent(currentUser)) {
            return result.and(EnrollmentSpecification.visibleForStudent(currentUser.getId()));
        }

        if (LmsAccessControl.isParent(currentUser)) {
            return result.and(EnrollmentSpecification.visibleForParent(currentUser.getId()));
        }

        throw new AccessDeniedException("You do not have permission to view enrollments");
    }

    private void validateCreateRequest(EnrollmentCreateRequest request) {
        if (request.getGroupId() == null) {
            throw new CustomBadRequestException("Group id is required");
        }

        if (request.getStudentProfileId() == null) {
            throw new CustomBadRequestException("Student profile id is required");
        }
    }

    private void validateGroupCapacity(LmsGroup group, EnrollmentStatus newStatus, Enrollment currentEnrollment) {
        if (group.getCapacity() == null || newStatus != EnrollmentStatus.ACTIVE) {
            return;
        }

        long activeCount = enrollmentRepository.countByGroupIdAndStatus(group.getId(), EnrollmentStatus.ACTIVE);
        if (currentEnrollment != null
                && Objects.equals(currentEnrollment.getGroup().getId(), group.getId())
                && currentEnrollment.getStatus() == EnrollmentStatus.ACTIVE) {
            activeCount -= 1;
        }

        if (activeCount >= group.getCapacity()) {
            throw new CustomConflictException("Group capacity exceeded");
        }
    }
}
