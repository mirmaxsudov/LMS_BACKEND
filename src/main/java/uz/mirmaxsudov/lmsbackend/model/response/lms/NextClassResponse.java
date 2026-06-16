package uz.mirmaxsudov.lmsbackend.model.response.lms;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
@Builder
public class NextClassResponse {
    private String subject;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private OffsetDateTime startTime;
    private String dayLabel;
}
