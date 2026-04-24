package uz.mirmaxsudov.lmsbackend.repository.lms.group;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.Group;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.GroupStatus;

import java.util.ArrayList;
import java.util.List;

public class GroupSpecification {
    public static Specification<Group> filter(GroupFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("deleted"), Boolean.FALSE));

            if (filter.getCourseId() != null)
                predicates.add(cb.equal(root.get("course").get("id"), filter.getCourseId()));

            if (filter.getTeacherId() != null)
                predicates.add(cb.equal(root.get("teacher").get("id"), filter.getTeacherId()));

            if (filter.getStatus() != null)
                predicates.add(cb.equal(root.get("status"), filter.getStatus()));
            else if (filter.getActive() != null) {
                if (filter.getActive())
                    predicates.add(cb.equal(root.get("status"), GroupStatus.ACTIVE));
                else
                    predicates.add(cb.notEqual(root.get("status"), GroupStatus.ACTIVE));
            }

            if (filter.getMinCapacity() != null)
                predicates.add(cb.greaterThanOrEqualTo(root.get("capacity"), filter.getMinCapacity()));

            if (filter.getMaxCapacity() != null)
                predicates.add(cb.lessThanOrEqualTo(root.get("capacity"), filter.getMaxCapacity()));

            if (filter.getSearch() != null && !filter.getSearch().isBlank()) {
                String pattern = "%" + filter.getSearch().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("groupName")), pattern),
                        cb.like(cb.lower(root.get("course").get("title")), pattern),
                        cb.like(cb.lower(root.get("teacher").get("user").get("firstName")), pattern),
                        cb.like(cb.lower(root.get("teacher").get("user").get("lastName")), pattern),
                        cb.like(cb.lower(root.get("teacher").get("user").get("email")), pattern)
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
