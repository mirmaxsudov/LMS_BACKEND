package uz.mirmaxsudov.lmsbackend.service.base;

import org.springframework.http.ResponseEntity;
import uz.mirmaxsudov.lmsbackend.model.response.ApiResponse;
import uz.mirmaxsudov.lmsbackend.model.response.statistic.LandingStatistic;

public interface LandingStatisticsService {
    ResponseEntity<ApiResponse<LandingStatistic>> getLandingStatistic();
}
