package uz.mirmaxsudov.lmsbackend.model.response.lms;

import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
public class StudentWeekScheduleResponse {
    private WeekRangeResponse weekRange;
    private WeekSummaryResponse summary;
    private List<ScheduleDayResponse> days;
}
