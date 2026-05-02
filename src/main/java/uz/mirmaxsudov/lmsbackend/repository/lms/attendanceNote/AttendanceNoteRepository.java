package uz.mirmaxsudov.lmsbackend.repository.lms.attendanceNote;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.AttendanceNote;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AttendanceNoteRepository extends JpaRepository<AttendanceNote, UUID>, JpaSpecificationExecutor<AttendanceNote> {
    Optional<AttendanceNote> findByIdAndDeletedFalse(UUID id);

    Optional<AttendanceNote> findByAttendanceId(UUID attendanceId);

    boolean existsByAttendanceIdAndDeletedFalse(UUID attendanceId);

    boolean existsByAttendanceIdAndIdNotAndDeletedFalse(UUID attendanceId, UUID id);
}
