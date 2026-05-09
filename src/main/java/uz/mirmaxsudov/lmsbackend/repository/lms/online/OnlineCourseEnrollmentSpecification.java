package uz.mirmaxsudov.lmsbackend.repository.lms.online;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.online.OnlineCourseEnrollment;

import java.util.ArrayList;
import java.util.List;

public class OnlineCourseEnrollmentSpecification {
    public static Specification<OnlineCourseEnrollment> filter(OnlineCourseEnrollmentFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("deleted"), Boolean.FALSE));
            predicates.add(cb.equal(root.get("course").get("deleted"), Boolean.FALSE));
            predicates.add(cb.equal(root.get("student").get("deleted"), Boolean.FALSE));

            if (filter.getCourseId() != null)
                predicates.add(cb.equal(root.get("course").get("id"), filter.getCourseId()));

            if (filter.getStudentProfileId() != null)
                predicates.add(cb.equal(root.get("student").get("id"), filter.getStudentProfileId()));

            if (filter.getStatus() != null)
                predicates.add(cb.equal(root.get("status"), filter.getStatus()));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
