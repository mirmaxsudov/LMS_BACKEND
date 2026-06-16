package uz.mirmaxsudov.lmsbackend.model.response.lms;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WeekSummaryResponse {
    private int totalClasses;
    private long totalHours;
    private NextClassResponse nextClass;
}
