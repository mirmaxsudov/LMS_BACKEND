package uz.mirmaxsudov.lmsbackend.repository.user.specification;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import uz.mirmaxsudov.lmsbackend.model.entity.user.TeacherProfile;
import uz.mirmaxsudov.lmsbackend.repository.user.specification.dto.TeacherProfileFilter;

import java.util.ArrayList;
import java.util.List;

public class TeacherProfileSpecification {
    public static Specification<TeacherProfile> filter(TeacherProfileFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getPosition() != null)
                predicates.add(cb.equal(root.get("position"), filter.getPosition()));

            if (filter.getSearch() != null)
                predicates.add(cb.like(root.get("fullName"), "%" + filter.getSearch() + "%"));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
