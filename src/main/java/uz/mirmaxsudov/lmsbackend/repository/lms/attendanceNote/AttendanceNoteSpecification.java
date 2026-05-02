package uz.mirmaxsudov.lmsbackend.repository.lms.attendanceNote;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.AttendanceNote;

import java.util.ArrayList;
import java.util.List;

public class AttendanceNoteSpecification {
    public static Specification<AttendanceNote> filter(AttendanceNoteFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("deleted"), Boolean.FALSE));

            if (filter.getAttendanceId() != null)
                predicates.add(cb.equal(root.get("attendance").get("id"), filter.getAttendanceId()));

            if (filter.getStudentId() != null)
                predicates.add(cb.equal(root.get("attendance").get("student").get("id"), filter.getStudentId()));

            if (filter.getLessonSessionId() != null)
                predicates.add(cb.equal(root.get("attendance").get("lessonSession").get("id"), filter.getLessonSessionId()));

            if (filter.getGroupId() != null)
                predicates.add(cb.equal(root.get("attendance").get("lessonSession").get("group").get("id"), filter.getGroupId()));

            if (filter.getLessonId() != null)
                predicates.add(cb.equal(root.get("attendance").get("lessonSession").get("lesson").get("id"), filter.getLessonId()));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
