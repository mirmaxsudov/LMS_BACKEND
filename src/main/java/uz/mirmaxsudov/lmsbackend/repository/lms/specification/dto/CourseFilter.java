package uz.mirmaxsudov.lmsbackend.repository.lms.specification.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class CourseFilter {
    private String search;
    private UUID teacherId;
    private Boolean active;
}
