package uz.mirmaxsudov.lmsbackend.model.response.lms;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Builder
public class ScheduleClassResponse {
    private UUID id;
    private String subject;
    private String topic;
    private String teacherName;
    private String roomName;
    private String building;
    private UUID groupId;
    private String groupName;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private OffsetDateTime startTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private OffsetDateTime endTime;
    private String status;
}
