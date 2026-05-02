package uz.mirmaxsudov.lmsbackend.repository.lms.schedule;

import lombok.Builder;
import lombok.Getter;

import java.time.DayOfWeek;
import java.util.UUID;

@Getter
@Builder
public class ScheduleFilter {
    private UUID groupId;
    private DayOfWeek dayOfWeek;
}
