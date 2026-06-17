package uz.mirmaxsudov.lmsbackend.common.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uz.mirmaxsudov.lmsbackend.service.base.lms.AnnouncementService;

@Slf4j
@Component
@RequiredArgsConstructor
public class AnnouncementScheduler {

    private final AnnouncementService announcementService;

    @Scheduled(cron = "${announcement.scheduler.publish-cron:0 * * * * *}")
    public void publishDueScheduledAnnouncements() {
        try {
            int published = announcementService.publishDueScheduledAnnouncements();
            if (published > 0)
                log.info("Auto-published {} scheduled announcement(s)", published);
        } catch (Exception e) {
            log.error("Failed to publish due scheduled announcements", e);
        }
    }
}