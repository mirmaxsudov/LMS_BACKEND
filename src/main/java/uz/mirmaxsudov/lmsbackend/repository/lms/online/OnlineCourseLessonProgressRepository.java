package uz.mirmaxsudov.lmsbackend.repository.lms.online;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.online.OnlineCourseLessonProgress;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OnlineCourseLessonProgressRepository extends JpaRepository<OnlineCourseLessonProgress, UUID> {
    Optional<OnlineCourseLessonProgress> findByEnrollmentIdAndLessonIdAndDeletedFalse(UUID enrollmentId, UUID lessonId);
}
