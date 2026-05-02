package uz.mirmaxsudov.lmsbackend.repository.lms.lessonSession;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.LessonSession;

import java.util.ArrayList;
import java.util.List;

public class LessonSessionSpecification {
    public static Specification<LessonSession> filter(LessonSessionFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("deleted"), Boolean.FALSE));

            if (filter.getGroupId() != null)
                predicates.add(cb.equal(root.get("group").get("id"), filter.getGroupId()));

            if (filter.getLessonId() != null)
                predicates.add(cb.equal(root.get("lesson").get("id"), filter.getLessonId()));

            if (filter.getStatus() != null)
                predicates.add(cb.equal(root.get("status"), filter.getStatus()));

            if (filter.getFrom() != null)
                predicates.add(cb.greaterThanOrEqualTo(root.get("startTime"), filter.getFrom()));

            if (filter.getTo() != null)
                predicates.add(cb.lessThanOrEqualTo(root.get("startTime"), filter.getTo()));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
