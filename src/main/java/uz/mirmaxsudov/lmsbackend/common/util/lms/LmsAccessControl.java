package uz.mirmaxsudov.lmsbackend.common.util.lms;

import org.springframework.security.access.AccessDeniedException;
import uz.mirmaxsudov.lmsbackend.model.entity.auth.User;
import uz.mirmaxsudov.lmsbackend.model.enums.auth.SystemRole;

public final class LmsAccessControl {
    private LmsAccessControl() {
    }

    public static boolean hasRole(User user, SystemRole role) {
        if (user == null || user.getRoles() == null)
            return false;

        return user.getRoles().stream()
                .anyMatch(r -> r.getName() != null && r.getName().equalsIgnoreCase(role.name()));
    }

    public static boolean isAdmin(User user) {
        return hasRole(user, SystemRole.ADMIN) || hasRole(user, SystemRole.SUPER_ADMIN);
    }

    public static boolean isTeacher(User user) {
        return hasRole(user, SystemRole.TEACHER) || hasRole(user, SystemRole.SUPPORT_TEACHER);
    }

    public static boolean isStudent(User user) {
        return hasRole(user, SystemRole.STUDENT);
    }

    public static boolean isParent(User user) {
        return hasRole(user, SystemRole.PARENT) || hasRole(user, SystemRole.GUARDIAN);
    }

    public static void requireReadRole(User user) {
        if (isAdmin(user) || isTeacher(user) || isStudent(user) || isParent(user))
            return;

        throw new AccessDeniedException("You do not have permission for LMS read operations");
    }

    public static void requireWriteRole(User user) {
        if (isAdmin(user) || isTeacher(user))
            return;

        throw new AccessDeniedException("Only admin or teacher can modify LMS resources");
    }
}
