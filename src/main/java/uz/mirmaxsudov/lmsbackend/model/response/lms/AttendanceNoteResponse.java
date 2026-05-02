package uz.mirmaxsudov.lmsbackend.model.response.lms;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class AttendanceNoteResponse {
    private UUID id;
    private UUID attendanceId;
    private UUID studentProfileId;
    private String studentFullName;
    private UUID lessonSessionId;
    private UUID groupId;
    private String groupName;
    private UUID lessonId;
    private String lessonTitle;
    private String note;
}
