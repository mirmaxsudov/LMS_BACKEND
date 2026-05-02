package uz.mirmaxsudov.lmsbackend.model.response.lms;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.GroupScheduleType;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.GroupStatus;
import uz.mirmaxsudov.lmsbackend.model.response.course.GroupTeacherResponse;

import java.time.DayOfWeek;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Builder
public class GroupResponse {
    private UUID id;
    private String name;
    private UUID courseId;
    private String courseName;
    private GroupTeacherResponse teacher;
    private Integer capacity;
    private GroupScheduleType scheduleType;
    private Set<DayOfWeek> scheduleDays;
    private boolean active;
    private int currentStudents;
    private GroupStatus status;

}
