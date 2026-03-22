package uz.mirmaxsudov.lmsbackend.repository.lms.specification.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class GroupFilter {
    private String search;
    private UUID courseId;
    private UUID teacherId;
    private Boolean active;
}
