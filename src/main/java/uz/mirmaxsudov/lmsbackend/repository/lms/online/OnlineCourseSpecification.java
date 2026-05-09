package uz.mirmaxsudov.lmsbackend.repository.lms.online;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.online.OnlineCourse;

import java.util.ArrayList;
import java.util.List;

public class OnlineCourseSpecification {
    public static Specification<OnlineCourse> filter(OnlineCourseFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("deleted"), Boolean.FALSE));

            if (filter.getLevel() != null)
                predicates.add(cb.equal(root.get("level"), filter.getLevel()));

            if (filter.getStatus() != null)
                predicates.add(cb.equal(root.get("status"), filter.getStatus()));

            if (filter.getCreatedById() != null)
                predicates.add(cb.equal(root.get("createdBy").get("id"), filter.getCreatedById()));

            if (filter.getSearch() != null && !filter.getSearch().isBlank()) {
                String pattern = "%" + filter.getSearch().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("title")), pattern),
                        cb.like(cb.lower(root.get("slug")), pattern),
                        cb.like(cb.lower(root.get("shortDescription")), pattern),
                        cb.like(cb.lower(root.get("description")), pattern)
                ));
            }

            if (filter.getMinDurationInMinutes() != null)
                predicates.add(cb.greaterThanOrEqualTo(root.get("estimatedDurationInMinutes"), filter.getMinDurationInMinutes()));

            if (filter.getMaxDurationInMinutes() != null)
                predicates.add(cb.lessThanOrEqualTo(root.get("estimatedDurationInMinutes"), filter.getMaxDurationInMinutes()));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
