package uz.mirmaxsudov.lmsbackend.model.request.lms;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.GroupScheduleType;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.GroupStatus;

import java.time.DayOfWeek;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class GroupUpdateRequest {
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

    @NotNull(message = "Active flag is required")
    private Boolean active;

    @NotNull(message = "Status is required")
    private GroupStatus status;
}
