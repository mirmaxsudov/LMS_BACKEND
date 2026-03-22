package uz.mirmaxsudov.lmsbackend.repository.lms;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.Semester;

import java.util.List;
import java.util.UUID;

@Repository
public interface SemesterRepository extends JpaRepository<Semester, UUID> {
    List<Semester> findByCourseId(UUID courseId);

    List<Semester> findByCourseIdAndIsActive(UUID courseId, boolean isActive);
}
