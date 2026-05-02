package uz.mirmaxsudov.lmsbackend.model.request.lms;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class AttendanceNoteCreateRequest {
    @NotNull(message = "Attendance id is required")
    private UUID attendanceId;

    @NotBlank(message = "Note is required")
    @Size(max = 1000, message = "Note must be less than or equal to 1000 characters")
    private String note;
}
