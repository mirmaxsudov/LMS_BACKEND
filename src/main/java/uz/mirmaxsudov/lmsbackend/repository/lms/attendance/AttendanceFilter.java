package uz.mirmaxsudov.lmsbackend.repository.lms.attendance;

import lombok.Builder;
import lombok.Getter;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.AttendanceStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class AttendanceFilter {
    private UUID studentId;
    private UUID lessonSessionId;
    private UUID groupId;
    private UUID lessonId;
    private AttendanceStatus status;
    private LocalDateTime from;
    private LocalDateTime to;
}
