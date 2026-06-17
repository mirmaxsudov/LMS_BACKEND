package uz.mirmaxsudov.lmsbackend.model.request.lms;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnnouncementPinRequest {

    @NotNull(message = "pinned flag is required")
    private Boolean pinned;
}
