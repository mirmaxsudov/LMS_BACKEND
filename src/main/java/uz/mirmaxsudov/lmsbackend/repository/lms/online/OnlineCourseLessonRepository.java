package uz.mirmaxsudov.lmsbackend.repository.lms.online;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.online.OnlineCourseLesson;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OnlineCourseLessonRepository extends JpaRepository<OnlineCourseLesson, UUID> {
    Optional<OnlineCourseLesson> findByIdAndDeletedFalse(UUID id);

    List<OnlineCourseLesson> findByModuleIdAndDeletedFalseOrderByOrderIndexAsc(UUID moduleId);

    boolean existsByModuleIdAndOrderIndexAndDeletedFalse(UUID moduleId, Integer orderIndex);

    boolean existsByModuleIdAndOrderIndexAndIdNotAndDeletedFalse(UUID moduleId, Integer orderIndex, UUID id);
}
