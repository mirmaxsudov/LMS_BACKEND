package uz.mirmaxsudov.lmsbackend.repository.lms.section;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.CourseSection;

import java.util.ArrayList;
import java.util.List;

public class CourseSectionSpecification {
    public static Specification<CourseSection> filter(CourseSectionFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("deleted"), Boolean.FALSE));

            if (filter.getCourseId() != null)
                predicates.add(cb.equal(root.get("course").get("id"), filter.getCourseId()));

            if (filter.getSearch() != null && !filter.getSearch().isBlank()) {
                String pattern = "%" + filter.getSearch().toLowerCase() + "%";
                predicates.add(cb.like(cb.lower(root.get("title")), pattern));
            }

            if (filter.getOrderIndex() != null)
                predicates.add(cb.equal(root.get("orderIndex"), filter.getOrderIndex()));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
