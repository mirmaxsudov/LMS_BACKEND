package uz.mirmaxsudov.lmsbackend.repository.lms.lesson;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.Lesson;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, UUID>, JpaSpecificationExecutor<Lesson> {
    Optional<Lesson> findByIdAndDeletedFalse(UUID id);

    @Query("""
            select l from Lesson l
            join l.section s
            where s.course.id = :courseId
              and l.deleted = false
              and s.deleted = false
            order by s.orderIndex asc, l.createdAt asc
            """)
    List<Lesson> findActiveByCourseIdOrderBySectionAndCreatedAt(@Param("courseId") UUID courseId);

    @Query("""
            select s.course.id as courseId, count(l) as lessonCount
            from Lesson l
            join l.section s
            where s.course.id in :courseIds
              and l.deleted = false
              and s.deleted = false
            group by s.course.id
            """)
    List<CourseLessonCount> countActiveByCourseIds(@Param("courseIds") List<UUID> courseIds);

    interface CourseLessonCount {
        UUID getCourseId();

        long getLessonCount();
    }
}
