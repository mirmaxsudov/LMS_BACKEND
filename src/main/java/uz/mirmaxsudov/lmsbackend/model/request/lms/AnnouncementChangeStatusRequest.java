package uz.mirmaxsudov.lmsbackend.model.request.lms;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.AnnouncementStatus;

@Getter
@Setter
public class AnnouncementChangeStatusRequest {

    @NotNull(message = "Status is required")
    private AnnouncementStatus status;
}
