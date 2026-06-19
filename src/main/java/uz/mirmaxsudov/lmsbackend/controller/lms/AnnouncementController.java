package uz.mirmaxsudov.lmsbackend.controller.lms;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uz.mirmaxsudov.lmsbackend.common.util.APIUtil;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.AnnouncementAudience;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.AnnouncementPriority;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.AnnouncementStatus;
import uz.mirmaxsudov.lmsbackend.model.request.lms.AnnouncementChangeStatusRequest;
import uz.mirmaxsudov.lmsbackend.model.request.lms.AnnouncementCreateRequest;
import uz.mirmaxsudov.lmsbackend.model.request.lms.AnnouncementPinRequest;
import uz.mirmaxsudov.lmsbackend.model.request.lms.AnnouncementUpdateRequest;
import uz.mirmaxsudov.lmsbackend.model.response.ApiPaginateResponse;
import uz.mirmaxsudov.lmsbackend.model.response.ApiResponse;
import uz.mirmaxsudov.lmsbackend.model.response.lms.AnnouncementOverviewResponse;
import uz.mirmaxsudov.lmsbackend.model.response.lms.AnnouncementResponse;
import uz.mirmaxsudov.lmsbackend.security.service.CustomUserDetails;
import uz.mirmaxsudov.lmsbackend.service.base.lms.AnnouncementService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(APIUtil.API_BASE_URL + "announcements")
public class AnnouncementController {

    private final AnnouncementService announcementService;

    @GetMapping
    public ResponseEntity<ApiPaginateResponse<List<AnnouncementResponse>>> getAll(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "status", required = false) AnnouncementStatus status,
            @RequestParam(value = "priority", required = false) AnnouncementPriority priority,
            @RequestParam(value = "audience", required = false) AnnouncementAudience audience,
            @RequestParam(value = "pinned", required = false) Boolean pinned,
            @RequestParam(value = "authorId", required = false) UUID authorId,
            @AuthenticationPrincipal CustomUserDetails details
    ) {
        return announcementService.getAll(page, size, search, status, priority, audience, pinned, authorId, details.roles());
    }

    @GetMapping("/overview")
    public ResponseEntity<ApiResponse<AnnouncementOverviewResponse>> getOverview() {
        return announcementService.getOverview();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AnnouncementResponse>> getById(@PathVariable UUID id) {
        return announcementService.getByIdResponse(id);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AnnouncementResponse>> create(
            @RequestBody @Valid AnnouncementCreateRequest request,
            @AuthenticationPrincipal CustomUserDetails details
    ) {
        return announcementService.createAnnouncement(request, details.user());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AnnouncementResponse>> update(
            @PathVariable UUID id,
            @RequestBody @Valid AnnouncementUpdateRequest request
    ) {
        return announcementService.updateAnnouncement(id, request);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<AnnouncementResponse>> changeStatus(
            @PathVariable UUID id,
            @RequestBody @Valid AnnouncementChangeStatusRequest request
    ) {
        return announcementService.changeStatus(id, request);
    }

    @PatchMapping("/{id}/pin")
    public ResponseEntity<ApiResponse<AnnouncementResponse>> changePin(
            @PathVariable UUID id,
            @RequestBody @Valid AnnouncementPinRequest request
    ) {
        return announcementService.changePin(id, request);
    }

    @PatchMapping("/{id}/view")
    public ResponseEntity<ApiResponse<AnnouncementResponse>> recordView(@PathVariable UUID id) {
        return announcementService.recordView(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        return announcementService.deleteAnnouncement(id);
    }
}
