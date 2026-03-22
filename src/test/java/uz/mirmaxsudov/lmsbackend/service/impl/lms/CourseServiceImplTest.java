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
import uz.mirmaxsudov.lmsbackend.model.request.lms.CourseCreateRequest;
import uz.mirmaxsudov.lmsbackend.repository.UserRepository;
import uz.mirmaxsudov.lmsbackend.repository.lms.CourseRepository;
import uz.mirmaxsudov.lmsbackend.security.service.CustomUserDetails;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CourseServiceImplTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CourseServiceImpl service;

    private User teacherUser;
    private User anotherTeacher;

    @BeforeEach
    void setUp() {
        teacherUser = userWithRole("TEACHER");
        anotherTeacher = userWithRole("TEACHER");
    }

    @Test
    void create_shouldThrowAccessDenied_whenTeacherCreatesForAnotherTeacher() {
        CourseCreateRequest request = new CourseCreateRequest();
        request.setName("Math");
        request.setCode("MTH-1");
        request.setTeacherId(anotherTeacher.getId());

        when(userRepository.findById(anotherTeacher.getId())).thenReturn(java.util.Optional.of(anotherTeacher));

        assertThrows(AccessDeniedException.class, () -> service.create(request, new CustomUserDetails(teacherUser)));
    }

    @Test
    void create_shouldThrowConflict_whenCodeAlreadyExists() {
        CourseCreateRequest request = new CourseCreateRequest();
        request.setName("Math");
        request.setCode("MTH-1");
        request.setTeacherId(teacherUser.getId());

        when(userRepository.findById(teacherUser.getId())).thenReturn(java.util.Optional.of(teacherUser));
        when(courseRepository.existsByCodeIgnoreCase("MTH-1")).thenReturn(true);

        assertThrows(CustomConflictException.class, () -> service.create(request, new CustomUserDetails(teacherUser)));
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
