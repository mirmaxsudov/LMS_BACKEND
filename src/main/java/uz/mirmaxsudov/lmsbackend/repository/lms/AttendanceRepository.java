package uz.mirmaxsudov.lmsbackend.repository.lms;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.Attendance;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, UUID> {
    Optional<Attendance> findByLectureIdAndStudentProfileId(UUID lectureId, UUID studentProfileId);

    List<Attendance> findByLectureId(UUID lectureId);

    List<Attendance> findByStudentProfileId(UUID studentProfileId);
}
