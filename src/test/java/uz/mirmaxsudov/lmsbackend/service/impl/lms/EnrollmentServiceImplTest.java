package uz.mirmaxsudov.lmsbackend.service.impl.lms;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import uz.mirmaxsudov.lmsbackend.exceptions.CustomConflictException;
import uz.mirmaxsudov.lmsbackend.model.entity.auth.Role;
import uz.mirmaxsudov.lmsbackend.model.entity.auth.User;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.LmsGroup;
import uz.mirmaxsudov.lmsbackend.model.entity.user.StudentProfile;
import uz.mirmaxsudov.lmsbackend.model.request.lms.EnrollmentCreateRequest;
import uz.mirmaxsudov.lmsbackend.repository.lms.EnrollmentRepository;
import uz.mirmaxsudov.lmsbackend.repository.lms.LmsGroupRepository;
import uz.mirmaxsudov.lmsbackend.repository.user.StudentProfileRepository;
import uz.mirmaxsudov.lmsbackend.security.service.CustomUserDetails;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EnrollmentServiceImplTest {

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private LmsGroupRepository groupRepository;

    @Mock
    private StudentProfileRepository studentProfileRepository;

    @InjectMocks
    private EnrollmentServiceImpl service;

    private User teacher;
    private LmsGroup group;

    @BeforeEach
    void setUp() {
        teacher = userWithRole("TEACHER");

        group = new LmsGroup();
        group.setId(UUID.randomUUID());
        group.setTeacher(teacher);
        group.setCapacity(1);
    }

    @Test
    void create_shouldThrowConflict_whenDuplicateEnrollmentExists() {
        StudentProfile studentProfile = new StudentProfile();
        studentProfile.setId(UUID.randomUUID());

        EnrollmentCreateRequest request = new EnrollmentCreateRequest();
        request.setGroupId(group.getId());
        request.setStudentProfileId(studentProfile.getId());

        when(groupRepository.findById(group.getId())).thenReturn(Optional.of(group));
        when(studentProfileRepository.findById(studentProfile.getId())).thenReturn(Optional.of(studentProfile));
        when(enrollmentRepository.existsByGroupIdAndStudentProfileId(group.getId(), studentProfile.getId())).thenReturn(true);

        assertThrows(CustomConflictException.class, () -> service.create(request, new CustomUserDetails(teacher)));
    }

    @Test
    void create_shouldThrowAccessDenied_whenTeacherTriesOtherGroup() {
        User anotherTeacher = userWithRole("TEACHER");
        group.setTeacher(anotherTeacher);

        StudentProfile studentProfile = new StudentProfile();
        studentProfile.setId(UUID.randomUUID());

        EnrollmentCreateRequest request = new EnrollmentCreateRequest();
        request.setGroupId(group.getId());
        request.setStudentProfileId(studentProfile.getId());

        when(groupRepository.findById(group.getId())).thenReturn(Optional.of(group));

        assertThrows(AccessDeniedException.class, () -> service.create(request, new CustomUserDetails(teacher)));
    }

    private User userWithRole(String roleName) {
        User user = new User();
        user.setId(UUID.randomUUID());

        Role role = new Role();
        role.setName(roleName);

        user.setRoles(Set.of(role));
        return user;
    }
}
