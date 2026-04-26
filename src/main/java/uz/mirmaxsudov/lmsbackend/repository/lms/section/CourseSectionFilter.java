package uz.mirmaxsudov.lmsbackend.repository.lms.section;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class CourseSectionFilter {
    private String search;
    private UUID courseId;
    private Integer orderIndex;
}
