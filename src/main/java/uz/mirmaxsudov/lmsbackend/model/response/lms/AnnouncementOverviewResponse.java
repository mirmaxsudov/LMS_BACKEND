package uz.mirmaxsudov.lmsbackend.model.response.lms;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AnnouncementOverviewResponse {
    private long published;
    private long scheduled;
    private long pinned;
    private long totalReach;
}
