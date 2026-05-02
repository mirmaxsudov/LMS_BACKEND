package uz.mirmaxsudov.lmsbackend.model.response.lms;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.AttendanceStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
public class AttendanceResponse {
    private UUID id;
    private UUID studentId;
    private UUID studentProfileId;
    private String studentFullName;
    private UUID lessonSessionId;
    private UUID groupId;
    private String groupName;
    private UUID lessonId;
    private String lessonTitle;
    private LocalDateTime lessonStartTime;
    private LocalDateTime lessonEndTime;
    private AttendanceStatus status;
}
