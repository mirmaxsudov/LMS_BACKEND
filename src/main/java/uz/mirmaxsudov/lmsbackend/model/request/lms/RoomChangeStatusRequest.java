package uz.mirmaxsudov.lmsbackend.model.request.lms;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.RoomStatus;

@Getter
@Setter
public class RoomChangeStatusRequest {

    @NotNull(message = "Status is required")
    private RoomStatus status;
}
