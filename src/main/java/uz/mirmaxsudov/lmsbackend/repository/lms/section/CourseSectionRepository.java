package uz.mirmaxsudov.lmsbackend.repository.lms.section;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.CourseSection;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CourseSectionRepository extends JpaRepository<CourseSection, UUID>, JpaSpecificationExecutor<CourseSection> {
    Optional<CourseSection> findByIdAndDeletedFalse(UUID id);
}
