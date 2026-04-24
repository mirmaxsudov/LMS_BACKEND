package uz.mirmaxsudov.lmsbackend.repository.user.specification;

import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import uz.mirmaxsudov.lmsbackend.model.entity.auth.User;
import uz.mirmaxsudov.lmsbackend.repository.user.specification.dto.UserFilter;

import java.util.ArrayList;
import java.util.List;

public class UserSpecification {
    public static Specification<User> filter(UserFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            query.distinct(true);
            predicates.add(cb.equal(root.get("deleted"), Boolean.FALSE));

            if (filter.getStatus() != null)
                predicates.add(cb.equal(root.get("status"), filter.getStatus()));

            if (filter.getRole() != null)
                predicates.add(cb.equal(
                        cb.upper(root.join("roles", JoinType.LEFT).get("name")),
                        filter.getRole().name()
                ));

            if (filter.getPermissionId() != null)
                predicates.add(cb.equal(
                        root.join("roles", JoinType.LEFT)
                                .join("permissions", JoinType.LEFT)
                                .get("id"),
                        filter.getPermissionId()
                ));

            if (filter.getSearch() != null && !filter.getSearch().isBlank()) {
                String pattern = "%" + filter.getSearch().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("firstName")), pattern),
                        cb.like(cb.lower(root.get("lastName")), pattern),
                        cb.like(cb.lower(root.get("middleName")), pattern),
                        cb.like(cb.lower(root.get("email")), pattern),
                        cb.like(cb.lower(root.get("phoneNumber")), pattern)
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
