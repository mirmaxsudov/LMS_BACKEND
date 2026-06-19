package uz.mirmaxsudov.lmsbackend.model.response.lms;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Builder
public class TeacherGroupStudentsResponse {
    private UUID groupId;
    private String groupName;
    private int totalStudents;
    private SyllabusProgressResponse progress;
    private Set<DayOfWeek> scheduleDays;
    private List<ScheduleResponse> schedules;
    private List<TeacherGroupStudentResponse> students;
}
