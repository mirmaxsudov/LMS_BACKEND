package uz.mirmaxsudov.lmsbackend.model.request.lms;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class GroupAddStudentsRequest {
    @NotEmpty(message = "At least one student id is required")
    private List<UUID> studentIds;
}
