package uz.mirmaxsudov.lmsbackend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uz.mirmaxsudov.lmsbackend.model.response.ApiResponse;
import uz.mirmaxsudov.lmsbackend.model.response.statistic.LandingStatistic;
import uz.mirmaxsudov.lmsbackend.service.base.LandingStatisticsService;
import uz.mirmaxsudov.lmsbackend.service.base.UserService;
import uz.mirmaxsudov.lmsbackend.service.base.lms.CourseService;

@Service
@RequiredArgsConstructor
public class LandingStatisticsServiceImpl implements LandingStatisticsService {
    private final UserService userService;
    private final CourseService courseService;

    @Override
    public ResponseEntity<ApiResponse<LandingStatistic>> getLandingStatistic() {
        int averageCompletionRate = 99, activeCourses = courseService.getActiveCoursesCount(), totalLearners = userService.getTotalUsers();

        LandingStatistic response = LandingStatistic.builder()
                .averageCompletionRate(averageCompletionRate)
                .activeCourses(activeCourses)
                .totalLearners(totalLearners)
                .build();

        return ResponseEntity.ok(ApiResponse.<LandingStatistic>builder()
                .message("Landing statistics retrieved successfully")
                .data(response)
                .success(true)
                .build());
    }
}