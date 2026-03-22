package uz.mirmaxsudov.lmsbackend.repository.lms.specification;

import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.LmsGroup;
import uz.mirmaxsudov.lmsbackend.repository.lms.specification.dto.GroupFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GroupSpecification {
    public static Specification<LmsGroup> filter(GroupFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getCourseId() != null) {
                predicates.add(cb.equal(root.get("course").get("id"), filter.getCourseId()));
            }

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
                        cb.like(cb.lower(root.get("course").get("name")), pattern)
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<LmsGroup> visibleForStudent(UUID studentUserId) {
        return (root, query, cb) -> {
            query.distinct(true);
            return cb.equal(
                    root.join("enrollments", JoinType.LEFT)
                            .join("studentProfile", JoinType.LEFT)
                            .join("user", JoinType.LEFT)
                            .get("id"),
                    studentUserId
            );
        };
    }

    public static Specification<LmsGroup> visibleForParent(UUID parentUserId) {
        return (root, query, cb) -> {
            query.distinct(true);
            return cb.equal(
                    root.join("enrollments", JoinType.LEFT)
                            .join("studentProfile", JoinType.LEFT)
                            .join("parents", JoinType.LEFT)
                            .join("user", JoinType.LEFT)
                            .get("id"),
                    parentUserId
            );
        };
    }
}
