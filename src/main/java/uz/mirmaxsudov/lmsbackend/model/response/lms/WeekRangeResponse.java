package uz.mirmaxsudov.lmsbackend.model.response.lms;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class WeekRangeResponse {
    private LocalDate from;
    private LocalDate to;
    private String label;
}
