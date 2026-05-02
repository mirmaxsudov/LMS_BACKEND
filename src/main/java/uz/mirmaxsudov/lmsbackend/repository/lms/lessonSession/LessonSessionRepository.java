package uz.mirmaxsudov.lmsbackend.repository.lms.lessonSession;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.LessonSession;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LessonSessionRepository extends JpaRepository<LessonSession, UUID>, JpaSpecificationExecutor<LessonSession> {
    Optional<LessonSession> findByIdAndDeletedFalse(UUID id);

    List<LessonSession> findAllByGroupIdAndDeletedFalseOrderByStartTimeAsc(UUID groupId);

    boolean existsByGroupIdAndStartTimeAndDeletedFalse(UUID groupId, LocalDateTime startTime);

    boolean existsByGroupIdAndLessonIdAndStartTimeAndDeletedFalse(UUID groupId, UUID lessonId, LocalDateTime startTime);

    boolean existsByGroupIdAndLessonIdAndStartTimeAndIdNotAndDeletedFalse(
            UUID groupId,
            UUID lessonId,
            LocalDateTime startTime,
            UUID id
    );
}
