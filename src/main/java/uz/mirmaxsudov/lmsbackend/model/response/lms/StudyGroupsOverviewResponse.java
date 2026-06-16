package uz.mirmaxsudov.lmsbackend.model.response.lms;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class StudyGroupsOverviewResponse {
    private long totalGroups;
    private long totalClassmates;
    private long sessionsThisWeek;
    private int averageProgress;
}
