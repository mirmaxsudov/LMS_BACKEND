package uz.mirmaxsudov.lmsbackend.repository.lms.online;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.online.OnlineCourseModule;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OnlineCourseModuleRepository extends JpaRepository<OnlineCourseModule, UUID> {
    Optional<OnlineCourseModule> findByIdAndDeletedFalse(UUID id);

    List<OnlineCourseModule> findByCourseIdAndDeletedFalseOrderByOrderIndexAsc(UUID courseId);

    boolean existsByCourseIdAndOrderIndexAndDeletedFalse(UUID courseId, Integer orderIndex);

    boolean existsByCourseIdAndOrderIndexAndIdNotAndDeletedFalse(UUID courseId, Integer orderIndex, UUID id);
}
