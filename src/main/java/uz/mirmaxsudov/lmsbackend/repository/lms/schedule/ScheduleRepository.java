package uz.mirmaxsudov.lmsbackend.repository.lms.schedule;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.Schedule;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, UUID>, JpaSpecificationExecutor<Schedule> {
    Optional<Schedule> findByIdAndDeletedFalse(UUID id);

    List<Schedule> findAllByGroupIdAndDeletedFalse(UUID groupId);

    boolean existsByGroupIdAndDeletedFalse(UUID groupId);

    boolean existsByGroupIdAndDayOfWeekAndStartTimeAndEndTimeAndDeletedFalse(
            UUID groupId,
            DayOfWeek dayOfWeek,
            LocalTime startTime,
            LocalTime endTime
    );

    boolean existsByGroupIdAndDayOfWeekAndStartTimeAndEndTimeAndIdNotAndDeletedFalse(
            UUID groupId,
            DayOfWeek dayOfWeek,
            LocalTime startTime,
            LocalTime endTime,
            UUID id
    );
}
