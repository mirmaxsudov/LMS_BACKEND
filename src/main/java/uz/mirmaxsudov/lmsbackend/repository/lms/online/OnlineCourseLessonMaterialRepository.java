package uz.mirmaxsudov.lmsbackend.repository.lms.online;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.online.OnlineCourseLessonMaterial;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OnlineCourseLessonMaterialRepository extends JpaRepository<OnlineCourseLessonMaterial, UUID> {
    Optional<OnlineCourseLessonMaterial> findByIdAndDeletedFalse(UUID id);

    boolean existsByLessonIdAndAttachmentIdAndDeletedFalse(UUID lessonId, UUID attachmentId);

    boolean existsByLessonIdAndAttachmentIdAndIdNotAndDeletedFalse(UUID lessonId, UUID attachmentId, UUID id);
}
