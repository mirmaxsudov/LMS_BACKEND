package uz.mirmaxsudov.lmsbackend.controller.statistics;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.mirmaxsudov.lmsbackend.annotations.OpenAuth;
import uz.mirmaxsudov.lmsbackend.common.util.APIUtil;
import uz.mirmaxsudov.lmsbackend.model.response.ApiResponse;
import uz.mirmaxsudov.lmsbackend.model.response.statistic.LandingStatistic;
import uz.mirmaxsudov.lmsbackend.service.base.LandingStatisticsService;

@RestController
@RequiredArgsConstructor
@RequestMapping(APIUtil.API_BASE_URL + "landing-statistics")
public class LandingStatisticsController {
    private final LandingStatisticsService landingStatisticsService;

    @OpenAuth
    @GetMapping
    public ResponseEntity<ApiResponse<LandingStatistic>> getLandingStatistic() {
        return landingStatisticsService.getLandingStatistic();
    }
}