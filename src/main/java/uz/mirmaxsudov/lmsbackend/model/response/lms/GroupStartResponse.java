package uz.mirmaxsudov.lmsbackend.model.response.lms;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class GroupStartResponse {
    private GroupResponse group;
    private List<ScheduleResponse> schedules;
}
