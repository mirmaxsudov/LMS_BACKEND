package uz.mirmaxsudov.lmsbackend.repository.lms.lesson;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.Lesson;

import java.util.ArrayList;
import java.util.List;

public class LessonSpecification {
    public static Specification<Lesson> filter(LessonFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("deleted"), Boolean.FALSE));

            if (filter.getSectionId() != null)
                predicates.add(cb.equal(root.get("section").get("id"), filter.getSectionId()));

            if (filter.getSearch() != null && !filter.getSearch().isBlank()) {
                String pattern = "%" + filter.getSearch().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("title")), pattern),
                        cb.like(cb.lower(root.get("content")), pattern)
                ));
            }

            if (filter.getMinDuration() != null)
                predicates.add(cb.greaterThanOrEqualTo(root.get("durationInMinutes"), filter.getMinDuration()));

            if (filter.getMaxDuration() != null)
                predicates.add(cb.lessThanOrEqualTo(root.get("durationInMinutes"), filter.getMaxDuration()));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
