package uz.mirmaxsudov.lmsbackend.repository.user.specification;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import uz.mirmaxsudov.lmsbackend.model.entity.user.ParentProfile;
import uz.mirmaxsudov.lmsbackend.repository.user.specification.dto.ParentProfileFilter;

import java.util.ArrayList;
import java.util.List;

public class ParentProfileSpecification {
    public static Specification<ParentProfile> filter(ParentProfileFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("deleted"), Boolean.FALSE));

            if (filter.getSearch() != null && !filter.getSearch().isBlank()) {
                var userJoin = root.join("user");
                String pattern = "%" + filter.getSearch().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(userJoin.get("firstName")), pattern),
                        cb.like(cb.lower(userJoin.get("lastName")), pattern),
                        cb.like(cb.lower(userJoin.get("middleName")), pattern),
                        cb.like(cb.lower(userJoin.get("email")), pattern),
                        cb.like(cb.lower(userJoin.get("phoneNumber")), pattern)
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
