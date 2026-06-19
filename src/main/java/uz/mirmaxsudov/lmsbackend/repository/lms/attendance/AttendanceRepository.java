package uz.mirmaxsudov.lmsbackend.repository.lms.attendance;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.Attendance;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.LessonSessionStatus;

import java.util.List;
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

    List<Attendance> findAllByStudent_IdAndLessonSession_StatusAndDeletedFalse(
            UUID studentId,
            LessonSessionStatus lessonSessionStatus
    );

    @Query("""
            select a from Attendance a
            join fetch a.student s
            join a.lessonSession ls
            where ls.group.id = :groupId
              and ls.status = :status
              and a.deleted = false
              and ls.deleted = false
              and s.deleted = false
            """)
    List<Attendance> findAllByGroupIdAndLessonSessionStatus(
            @Param("groupId") UUID groupId,
            @Param("status") LessonSessionStatus status
    );
}
