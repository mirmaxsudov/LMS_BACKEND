package uz.mirmaxsudov.lmsbackend.repository.search;

import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.Course;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.CourseSection;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.Lesson;

import java.util.ArrayList;
import java.util.List;

public class GlobalSearchSpecification {
    private GlobalSearchSpecification() {
    }

    public static Specification<Course> course(String pattern) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("deleted"), Boolean.FALSE));
            predicates.add(cb.or(
                    cb.like(cb.lower(root.get("title")), pattern),
                    cb.like(cb.lower(root.get("description")), pattern)
            ));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<CourseSection> section(String pattern) {
        return (root, query, cb) -> {
            var course = root.join("course", JoinType.INNER);

            if (query != null && CourseSection.class.equals(query.getResultType()))
                root.fetch("course", JoinType.INNER);

            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("deleted"), Boolean.FALSE));
            predicates.add(cb.equal(course.get("deleted"), Boolean.FALSE));
            predicates.add(cb.or(
                    cb.like(cb.lower(root.get("title")), pattern),
                    cb.like(cb.lower(course.get("title")), pattern)
            ));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Lesson> lesson(String pattern) {
        return (root, query, cb) -> {
            var section = root.join("section", JoinType.INNER);
            var course = section.join("course", JoinType.INNER);

            if (query != null && Lesson.class.equals(query.getResultType())) {
                var fetchedSection = root.fetch("section", JoinType.INNER);
                fetchedSection.fetch("course", JoinType.INNER);
            }

            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("deleted"), Boolean.FALSE));
            predicates.add(cb.equal(section.get("deleted"), Boolean.FALSE));
            predicates.add(cb.equal(course.get("deleted"), Boolean.FALSE));
            predicates.add(cb.or(
                    cb.like(cb.lower(root.get("title")), pattern),
                    cb.like(cb.lower(root.get("content")), pattern),
                    cb.like(cb.lower(section.get("title")), pattern),
                    cb.like(cb.lower(course.get("title")), pattern)
            ));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
