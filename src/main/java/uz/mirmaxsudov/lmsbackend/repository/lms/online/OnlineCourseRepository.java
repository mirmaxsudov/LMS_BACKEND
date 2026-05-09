package uz.mirmaxsudov.lmsbackend.repository.lms.online;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.online.OnlineCourse;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OnlineCourseRepository extends JpaRepository<OnlineCourse, UUID>, JpaSpecificationExecutor<OnlineCourse> {
    Optional<OnlineCourse> findByIdAndDeletedFalse(UUID id);

    boolean existsBySlug(String slug);

    boolean existsBySlugAndIdNot(String slug, UUID id);

    boolean existsBySlugAndDeletedFalse(String slug);

    boolean existsBySlugAndIdNotAndDeletedFalse(String slug, UUID id);
}
