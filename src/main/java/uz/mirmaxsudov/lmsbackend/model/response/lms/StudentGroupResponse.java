package uz.mirmaxsudov.lmsbackend.model.response.lms;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.GroupScheduleType;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.GroupStatus;
import uz.mirmaxsudov.lmsbackend.model.response.course.GroupTeacherResponse;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Builder
public class StudentGroupResponse {
    private UUID id;
    private String groupName;
    private GroupStatus status;
    private boolean active;
    private Integer capacity;
    private int currentStudents;
    private GroupScheduleType scheduleType;
    private Set<DayOfWeek> scheduleDays;
    private List<ScheduleResponse> schedules;
    private StudentGroupCourseResponse course;
    private GroupTeacherResponse teacher;
    private SyllabusProgressResponse syllabusProgress;
    private NextLessonResponse nextLesson;
    private int classmatesCount;
    private List<GroupClassmateResponse> classmates;
}
