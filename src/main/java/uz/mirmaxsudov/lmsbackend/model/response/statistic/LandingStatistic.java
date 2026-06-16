package uz.mirmaxsudov.lmsbackend.model.response.statistic;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LandingStatistic {
    private int activeCourses;
    private double averageCompletionRate;
    private int totalLearners;
}