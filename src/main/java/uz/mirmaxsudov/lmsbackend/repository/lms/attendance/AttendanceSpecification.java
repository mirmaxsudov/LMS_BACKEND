package uz.mirmaxsudov.lmsbackend.repository.lms.attendance;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.Attendance;

import java.util.ArrayList;
import java.util.List;

public class AttendanceSpecification {
    public static Specification<Attendance> filter(AttendanceFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("deleted"), Boolean.FALSE));

            if (filter.getStudentId() != null)
                predicates.add(cb.equal(root.get("student").get("id"), filter.getStudentId()));

            if (filter.getLessonSessionId() != null)
                predicates.add(cb.equal(root.get("lessonSession").get("id"), filter.getLessonSessionId()));

            if (filter.getGroupId() != null)
                predicates.add(cb.equal(root.get("lessonSession").get("group").get("id"), filter.getGroupId()));

            if (filter.getLessonId() != null)
                predicates.add(cb.equal(root.get("lessonSession").get("lesson").get("id"), filter.getLessonId()));

            if (filter.getStatus() != null)
                predicates.add(cb.equal(root.get("status"), filter.getStatus()));

            if (filter.getFrom() != null)
                predicates.add(cb.greaterThanOrEqualTo(root.get("lessonSession").get("startTime"), filter.getFrom()));

            if (filter.getTo() != null)
                predicates.add(cb.lessThanOrEqualTo(root.get("lessonSession").get("startTime"), filter.getTo()));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
