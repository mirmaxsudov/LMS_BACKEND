package uz.mirmaxsudov.lmsbackend.repository.lms.group;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.GroupStatus;

import java.util.UUID;

@Getter
@Setter
@Builder
public class GroupFilter {
    private String search;
    private UUID courseId;
    private UUID teacherId;
    private GroupStatus status;
    private Boolean active;
    private Integer minCapacity;
    private Integer maxCapacity;
}
