package uz.mirmaxsudov.lmsbackend.model.response.lms.online;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.online.OnlineCourseEnrollmentStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
public class OnlineCourseEnrollmentResponse {
    private UUID id;
    private OnlineCourseSummaryResponse course;
    private UUID studentProfileId;
    private UUID studentUserId;
    private String studentName;
    private UUID openedById;
    private OnlineCourseEnrollmentStatus status;
    private LocalDateTime openedAt;
    private LocalDateTime completedAt;
    private UUID currentModuleId;
    private UUID currentLessonId;
    private int completedLessons;
    private int totalLessons;
    private double progressPercentage;
    private List<OnlineCourseProgressResponse> moduleProgresses;
    private List<OnlineCourseProgressResponse> lessonProgresses;
}
