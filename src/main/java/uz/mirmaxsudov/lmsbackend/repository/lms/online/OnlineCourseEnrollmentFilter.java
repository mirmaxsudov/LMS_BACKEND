package uz.mirmaxsudov.lmsbackend.repository.lms.online;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.online.OnlineCourseEnrollmentStatus;

import java.util.UUID;

@Getter
@Setter
@Builder
public class OnlineCourseEnrollmentFilter {
    private UUID courseId;
    private UUID studentProfileId;
    private OnlineCourseEnrollmentStatus status;
}
