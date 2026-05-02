package uz.mirmaxsudov.lmsbackend.model.request.lms;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.GroupScheduleType;

import java.time.DayOfWeek;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class GroupCreateRequest {
    @NotBlank(message = "Group name is required")
    private String name;

    @NotNull(message = "Course id is required")
    private UUID courseId;

    @NotNull(message = "Teacher id is required")
    private UUID teacherId;

    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer capacity;

    @NotNull(message = "Schedule type is required")
    private GroupScheduleType scheduleType;

    private Set<DayOfWeek> scheduleDays;

    private Boolean active = true;
}
