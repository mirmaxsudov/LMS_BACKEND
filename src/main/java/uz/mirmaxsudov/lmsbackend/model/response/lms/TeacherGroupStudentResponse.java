package uz.mirmaxsudov.lmsbackend.model.response.lms;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.StudentEngagementStatus;
import uz.mirmaxsudov.lmsbackend.model.response.user.user.StudentProfileResponse;

@Getter
@Setter
@Builder
public class TeacherGroupStudentResponse {
    private StudentProfileResponse student;
    private int avgAttendance;
    private StudentEngagementStatus engagementStatus;
}
