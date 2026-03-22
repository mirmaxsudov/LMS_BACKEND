package uz.mirmaxsudov.lmsbackend.model.response.lms;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import uz.mirmaxsudov.lmsbackend.model.response.auth.AuthMe;

import java.util.UUID;

@Getter
@Setter
@Builder
public class CourseResponse {
    private UUID id;
    private String name;
    private String code;
    private String description;
    private boolean active;
    private UUID teacherId;
    private AuthMe teacher;
}
