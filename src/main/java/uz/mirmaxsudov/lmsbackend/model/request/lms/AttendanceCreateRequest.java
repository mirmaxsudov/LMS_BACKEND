package uz.mirmaxsudov.lmsbackend.model.request.lms;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.AttendanceStatus;

import java.util.UUID;

@Getter
@Setter
public class AttendanceCreateRequest {
    @NotNull(message = "Student id is required")
    private UUID studentId;

    @NotNull(message = "Lesson session id is required")
    private UUID lessonSessionId;

    @NotNull(message = "Attendance status is required")
    private AttendanceStatus status;
}
