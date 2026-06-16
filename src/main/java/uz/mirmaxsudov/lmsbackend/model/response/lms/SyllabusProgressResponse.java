package uz.mirmaxsudov.lmsbackend.model.response.lms;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SyllabusProgressResponse {
    private int completedLessons;
    private int totalLessons;
    private int percentage;
}
