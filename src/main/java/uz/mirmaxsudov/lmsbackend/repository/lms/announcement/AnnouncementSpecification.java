package uz.mirmaxsudov.lmsbackend.repository.lms.announcement;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.Announcement;

import java.util.ArrayList;
import java.util.List;

public class AnnouncementSpecification {

    public static Specification<Announcement> filter(AnnouncementFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("deleted"), Boolean.FALSE));

            if (filter.getStatus() != null)
                predicates.add(cb.equal(root.get("status"), filter.getStatus()));

            if (filter.getPriority() != null)
                predicates.add(cb.equal(root.get("priority"), filter.getPriority()));

            if (filter.getPinned() != null)
                predicates.add(cb.equal(root.get("pinned"), filter.getPinned()));

            if (filter.getAuthorId() != null)
                predicates.add(cb.equal(root.get("author").get("id"), filter.getAuthorId()));

            if (filter.getAudience() != null)
                predicates.add(cb.isMember(filter.getAudience(), root.get("audiences")));

            if (filter.getViewerAudiences() != null && !filter.getViewerAudiences().isEmpty()) {
                List<Predicate> audiencePredicates = new ArrayList<>();
                filter.getViewerAudiences().forEach(audience ->
                        audiencePredicates.add(cb.isMember(audience, root.get("audiences"))));
                predicates.add(cb.or(audiencePredicates.toArray(new Predicate[0])));
            }

            if (filter.getSearch() != null && !filter.getSearch().isBlank()) {
                String pattern = "%" + filter.getSearch().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("title")), pattern),
                        cb.like(cb.lower(root.get("content")), pattern)
                ));
            }

            // Pinned announcements first, then newest. Skip ordering on count queries.
            if (query != null && query.getResultType() != Long.class && query.getResultType() != long.class) {
                query.orderBy(
                        cb.desc(root.get("pinned")),
                        cb.desc(root.get("createdAt"))
                );
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
