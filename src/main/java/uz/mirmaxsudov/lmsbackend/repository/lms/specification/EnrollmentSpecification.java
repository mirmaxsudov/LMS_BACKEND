package uz.mirmaxsudov.lmsbackend.repository.lms.specification;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.Enrollment;
import uz.mirmaxsudov.lmsbackend.repository.lms.specification.dto.EnrollmentFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EnrollmentSpecification {
    public static Specification<Enrollment> filter(EnrollmentFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getGroupId() != null)
                predicates.add(cb.equal(root.get("group").get("id"), filter.getGroupId()));

            if (filter.getStudentProfileId() != null)
                predicates.add(cb.equal(root.get("studentProfile").get("id"), filter.getStudentProfileId()));

            if (filter.getStatus() != null)
                predicates.add(cb.equal(root.get("status"), filter.getStatus()));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Enrollment> visibleForTeacher(UUID teacherUserId) {
        return (root, query, cb) -> cb.equal(root.get("group").get("teacher").get("id"), teacherUserId);
    }

    public static Specification<Enrollment> visibleForStudent(UUID studentUserId) {
        return (root, query, cb) -> cb.equal(root.get("studentProfile").get("user").get("id"), studentUserId);
    }

    public static Specification<Enrollment> visibleForParent(UUID parentUserId) {
        return (root, query, cb) -> cb.equal(
                root.join("studentProfile")
                        .join("parents")
                        .join("user")
                        .get("id"),
                parentUserId
        );
    }
}
