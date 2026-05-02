package uz.mirmaxsudov.lmsbackend.repository.lms.attendance;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.Attendance;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, UUID>, JpaSpecificationExecutor<Attendance> {
    Optional<Attendance> findByIdAndDeletedFalse(UUID id);

    boolean existsByStudentIdAndLessonSessionIdAndDeletedFalse(UUID studentId, UUID lessonSessionId);

    boolean existsByStudentIdAndLessonSessionIdAndIdNotAndDeletedFalse(
            UUID studentId,
            UUID lessonSessionId,
            UUID id
    );
}
