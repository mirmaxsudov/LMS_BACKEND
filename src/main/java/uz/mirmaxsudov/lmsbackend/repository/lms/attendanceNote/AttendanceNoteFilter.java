package uz.mirmaxsudov.lmsbackend.repository.lms.attendanceNote;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class AttendanceNoteFilter {
    private UUID attendanceId;
    private UUID studentId;
    private UUID lessonSessionId;
    private UUID groupId;
    private UUID lessonId;
}
