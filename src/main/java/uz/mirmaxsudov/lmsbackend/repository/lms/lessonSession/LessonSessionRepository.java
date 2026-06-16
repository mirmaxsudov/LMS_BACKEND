package uz.mirmaxsudov.lmsbackend.repository.lms.lessonSession;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.LessonSession;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.LessonSessionStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LessonSessionRepository extends JpaRepository<LessonSession, UUID>, JpaSpecificationExecutor<LessonSession> {
    Optional<LessonSession> findByIdAndDeletedFalse(UUID id);

    List<LessonSession> findAllByGroupIdAndDeletedFalseOrderByStartTimeAsc(UUID groupId);

    boolean existsByGroupIdAndStartTimeAndDeletedFalse(UUID groupId, LocalDateTime startTime);

    boolean existsByGroupIdAndLessonIdAndStartTimeAndDeletedFalse(UUID groupId, UUID lessonId, LocalDateTime startTime);

    boolean existsByGroupIdAndLessonIdAndStartTimeAndIdNotAndDeletedFalse(
            UUID groupId,
            UUID lessonId,
            LocalDateTime startTime,
            UUID id
    );

    @Query("""
            select count(ls) from LessonSession ls
            join ls.group g
            join g.students s
            where s.id = :studentId
              and s.deleted = false
              and g.deleted = false
              and ls.deleted = false
              and ls.startTime >= :weekStart
              and ls.startTime < :weekEnd
            """)
    long countByStudentIdAndStartTimeBetween(
            @Param("studentId") UUID studentId,
            @Param("weekStart") LocalDateTime weekStart,
            @Param("weekEnd") LocalDateTime weekEnd
    );

    @Query("""
            select ls.group.id as groupId, count(ls) as sessionCount
            from LessonSession ls
            where ls.group.id in :groupIds
              and ls.status = :status
              and ls.deleted = false
            group by ls.group.id
            """)
    List<GroupSessionCount> countByGroupIdsAndStatus(
            @Param("groupIds") List<UUID> groupIds,
            @Param("status") LessonSessionStatus status
    );

    @Query("""
            select ls from LessonSession ls
            join fetch ls.lesson
            where ls.group.id in :groupIds
              and ls.status = :status
              and ls.startTime >= :from
              and ls.deleted = false
            order by ls.startTime asc
            """)
    List<LessonSession> findUpcomingByGroupIdsAndStatus(
            @Param("groupIds") List<UUID> groupIds,
            @Param("status") LessonSessionStatus status,
            @Param("from") LocalDateTime from
    );

    @Query("""
            select ls from LessonSession ls
            join fetch ls.lesson l
            join fetch ls.group g
            join fetch g.course c
            join fetch g.teacher t
            join fetch t.user tu
            left join fetch ls.room r
            join g.students s
            where s.id = :studentId
              and s.deleted = false
              and g.deleted = false
              and ls.deleted = false
              and ls.startTime >= :from
              and ls.startTime < :to
            order by ls.startTime asc
            """)
    List<LessonSession> findAllByStudentAndDateRange(
            @Param("studentId") UUID studentId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );

    interface GroupSessionCount {
        UUID getGroupId();

        long getSessionCount();
    }
}
