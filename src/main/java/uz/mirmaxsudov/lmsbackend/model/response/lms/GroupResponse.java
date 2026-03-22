package uz.mirmaxsudov.lmsbackend.model.response.lms;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import uz.mirmaxsudov.lmsbackend.model.response.auth.AuthMe;

import java.util.UUID;

@Getter
@Setter
@Builder
public class GroupResponse {
    private UUID id;
    private String name;
    private UUID courseId;
    private String courseName;
    private UUID teacherId;
    private AuthMe teacher;
    private Integer capacity;
    private boolean active;
    private int currentStudents;
}
