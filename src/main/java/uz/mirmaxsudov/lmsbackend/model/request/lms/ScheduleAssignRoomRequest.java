package uz.mirmaxsudov.lmsbackend.model.request.lms;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ScheduleAssignRoomRequest {
    // nullable — pass null to clear the room assignment
    private UUID roomId;
}
