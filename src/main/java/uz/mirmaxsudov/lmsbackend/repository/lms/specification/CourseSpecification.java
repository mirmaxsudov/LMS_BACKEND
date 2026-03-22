package uz.mirmaxsudov.lmsbackend.repository.lms.specification;

import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.Course;
import uz.mirmaxsudov.lmsbackend.repository.lms.specification.dto.CourseFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CourseSpecification {
    public static Specification<Course> filter(CourseFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getTeacherId() != null) {
                predicates.add(cb.equal(root.get("teacher").get("id"), filter.getTeacherId()));
            }

            if (filter.getActive() != null) {
                predicates.add(cb.equal(root.get("isActive"), filter.getActive()));
            }

            if (filter.getSearch() != null && !filter.getSearch().isBlank()) {
                String pattern = "%" + filter.getSearch().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("name")), pattern),
                        cb.like(cb.lower(root.get("code")), pattern),
                        cb.like(cb.lower(root.get("description")), pattern)
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Course> visibleForStudent(UUID studentUserId) {
        return (root, query, cb) -> {
            query.distinct(true);
            return cb.equal(
                    root.join("groups", JoinType.LEFT)
                            .join("enrollments", JoinType.LEFT)
                            .join("studentProfile", JoinType.LEFT)
                            .join("user", JoinType.LEFT)
                            .get("id"),
                    studentUserId
            );
        };
    }

    public static Specification<Course> visibleForParent(UUID parentUserId) {
        return (root, query, cb) -> {
            query.distinct(true);
            return cb.equal(
                    root.join("groups", JoinType.LEFT)
                            .join("enrollments", JoinType.LEFT)
                            .join("studentProfile", JoinType.LEFT)
                            .join("parents", JoinType.LEFT)
                            .join("user", JoinType.LEFT)
                            .get("id"),
                    parentUserId
            );
        };
    }
}
