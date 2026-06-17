package uz.mirmaxsudov.lmsbackend.service.base.lms;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import uz.mirmaxsudov.lmsbackend.model.entity.auth.User;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.Announcement;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.AnnouncementAudience;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.AnnouncementPriority;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.AnnouncementStatus;
import uz.mirmaxsudov.lmsbackend.model.request.lms.AnnouncementChangeStatusRequest;
import uz.mirmaxsudov.lmsbackend.model.request.lms.AnnouncementCreateRequest;
import uz.mirmaxsudov.lmsbackend.model.request.lms.AnnouncementPinRequest;
import uz.mirmaxsudov.lmsbackend.model.request.lms.AnnouncementUpdateRequest;
import uz.mirmaxsudov.lmsbackend.model.response.ApiPaginateResponse;
import uz.mirmaxsudov.lmsbackend.model.response.ApiResponse;
import uz.mirmaxsudov.lmsbackend.model.response.lms.AnnouncementResponse;
import uz.mirmaxsudov.lmsbackend.service.base.BaseCRUDService;

import java.util.List;
import java.util.UUID;

public interface AnnouncementService extends BaseCRUDService<Announcement> {

    ResponseEntity<ApiPaginateResponse<List<AnnouncementResponse>>> getAll(
            int page,
            int size,
            String search,
            AnnouncementStatus status,
            AnnouncementPriority priority,
            AnnouncementAudience audience,
            Boolean pinned,
            UUID authorId
    );

    ResponseEntity<ApiResponse<AnnouncementResponse>> getByIdResponse(UUID id);

    ResponseEntity<ApiResponse<AnnouncementResponse>> createAnnouncement(@Valid AnnouncementCreateRequest request, User author);

    ResponseEntity<ApiResponse<AnnouncementResponse>> updateAnnouncement(UUID id, @Valid AnnouncementUpdateRequest request);

    ResponseEntity<ApiResponse<AnnouncementResponse>> changeStatus(UUID id, @Valid AnnouncementChangeStatusRequest request);

    ResponseEntity<ApiResponse<AnnouncementResponse>> changePin(UUID id, @Valid AnnouncementPinRequest request);

    ResponseEntity<ApiResponse<AnnouncementResponse>> recordView(UUID id);

    ResponseEntity<ApiResponse<Void>> deleteAnnouncement(UUID id);

    /**
     * Publishes every SCHEDULED announcement whose {@code publishedAt} has reached the current time.
     *
     * @return the number of announcements that were published
     */
    int publishDueScheduledAnnouncements();
}
