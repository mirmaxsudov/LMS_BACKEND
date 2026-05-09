package uz.mirmaxsudov.lmsbackend.repository.lms.online;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.online.OnlineCourseModuleProgress;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OnlineCourseModuleProgressRepository extends JpaRepository<OnlineCourseModuleProgress, UUID> {
    Optional<OnlineCourseModuleProgress> findByEnrollmentIdAndModuleIdAndDeletedFalse(UUID enrollmentId, UUID moduleId);
}
