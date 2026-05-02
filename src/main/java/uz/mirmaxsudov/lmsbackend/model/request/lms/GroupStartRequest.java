package uz.mirmaxsudov.lmsbackend.model.request.lms;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GroupStartRequest {
    @Valid
    @NotEmpty(message = "At least one schedule is required")
    private List<GroupScheduleRequest> schedules;
}
