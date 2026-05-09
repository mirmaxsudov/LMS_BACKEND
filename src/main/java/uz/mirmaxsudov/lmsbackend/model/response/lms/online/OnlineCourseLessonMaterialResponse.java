package uz.mirmaxsudov.lmsbackend.model.response.lms.online;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class OnlineCourseLessonMaterialResponse {
    private UUID id;
    private UUID lessonId;
    private UUID attachmentId;
    private String attachmentUrl;
    private String title;
    private Integer orderIndex;
}
