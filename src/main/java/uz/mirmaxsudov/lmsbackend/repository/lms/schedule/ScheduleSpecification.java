package uz.mirmaxsudov.lmsbackend.repository.lms.schedule;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.Schedule;

import java.util.ArrayList;
import java.util.List;

public class ScheduleSpecification {
    public static Specification<Schedule> filter(ScheduleFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("deleted"), Boolean.FALSE));

            if (filter.getGroupId() != null)
                predicates.add(cb.equal(root.get("group").get("id"), filter.getGroupId()));

            if (filter.getDayOfWeek() != null)
                predicates.add(cb.equal(root.get("dayOfWeek"), filter.getDayOfWeek()));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
