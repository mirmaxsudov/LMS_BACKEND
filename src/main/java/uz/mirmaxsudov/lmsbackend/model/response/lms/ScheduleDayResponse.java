package uz.mirmaxsudov.lmsbackend.model.response.lms;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class ScheduleDayResponse {
    private String id;
    private LocalDate date;
    private int dayNumber;
    private String label;
    private String shortLabel;
    private Boolean isToday;
    private List<ScheduleClassResponse> classes;
}
