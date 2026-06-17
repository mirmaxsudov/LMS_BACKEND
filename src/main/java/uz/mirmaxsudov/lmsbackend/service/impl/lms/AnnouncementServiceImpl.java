package uz.mirmaxsudov.lmsbackend.service.impl.lms;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.mirmaxsudov.lmsbackend.common.filter.PageableBuilder;
import uz.mirmaxsudov.lmsbackend.common.util.mappers.AnnouncementMapper;
import uz.mirmaxsudov.lmsbackend.exceptions.CustomBadRequestException;
import uz.mirmaxsudov.lmsbackend.exceptions.CustomConflictException;
import uz.mirmaxsudov.lmsbackend.exceptions.CustomNotFoundException;
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
import uz.mirmaxsudov.lmsbackend.repository.lms.announcement.AnnouncementFilter;
import uz.mirmaxsudov.lmsbackend.repository.lms.announcement.AnnouncementRepository;
import uz.mirmaxsudov.lmsbackend.repository.lms.announcement.AnnouncementSpecification;
import uz.mirmaxsudov.lmsbackend.service.base.lms.AnnouncementService;
import uz.mirmaxsudov.lmsbackend.service.impl.BaseCRUDServiceImpl;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Service
public class AnnouncementServiceImpl extends BaseCRUDServiceImpl<Announcement, AnnouncementRepository> implements AnnouncementService {

    public AnnouncementServiceImpl(AnnouncementRepository repository) {
        super(repository);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<ApiPaginateResponse<List<AnnouncementResponse>>> getAll(
            int page,
            int size,
            String search,
            AnnouncementStatus status,
            AnnouncementPriority priority,
            AnnouncementAudience audience,
            Boolean pinned,
            UUID authorId
    ) {
        int normalizedPage = Math.max(page - 1, 0);
        int normalizedSize = size <= 0 ? 10 : size;

        Pageable pageable = PageableBuilder.build(normalizedPage, normalizedSize);

        Specification<Announcement> filter = AnnouncementSpecification.filter(AnnouncementFilter.builder()
                .search(search)
                .status(status)
                .priority(priority)
                .audience(audience)
                .pinned(pinned)
                .authorId(authorId)
                .build());

        Page<Announcement> announcements = repository.findAll(filter, pageable);
        List<AnnouncementResponse> results = announcements.getContent().stream()
                .map(AnnouncementMapper::toResponse)
                .toList();

        return ResponseEntity.ok(ApiPaginateResponse.<List<AnnouncementResponse>>builder()
                .success(true)
                .message("Announcements fetched successfully")
                .results(results)
                .total((int) announcements.getTotalElements())
                .page(announcements.getNumber() + 1)
                .size(announcements.getSize())
                .hasNext(announcements.hasNext())
                .build());
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<AnnouncementResponse>> getByIdResponse(UUID id) {
        Announcement announcement = findActiveAnnouncement(id);

        return ResponseEntity.ok(ApiResponse.<AnnouncementResponse>builder()
                .success(true)
                .message("Announcement fetched successfully")
                .data(AnnouncementMapper.toResponse(announcement))
                .build());
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponse<AnnouncementResponse>> createAnnouncement(AnnouncementCreateRequest request, User author) {
        AnnouncementStatus status = request.getStatus() != null ? request.getStatus() : AnnouncementStatus.DRAFT;
        LocalDateTime publishedAt = resolvePublishedAt(status, request.getPublishedAt(), null);
        validateScheduled(status, publishedAt);

        Announcement announcement = Announcement.builder()
                .title(request.getTitle().trim())
                .content(request.getContent().trim())
                .priority(request.getPriority())
                .audiences(new HashSet<>(request.getAudiences()))
                .pinned(request.isPinned())
                .status(status)
                .publishedAt(publishedAt)
                .viewCount(0L)
                .author(author)
                .build();

        Announcement saved = repository.save(announcement);

        return ResponseEntity.ok(ApiResponse.<AnnouncementResponse>builder()
                .success(true)
                .message("Announcement created successfully")
                .data(AnnouncementMapper.toResponse(saved))
                .build());
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponse<AnnouncementResponse>> updateAnnouncement(UUID id, AnnouncementUpdateRequest request) {
        Announcement existing = findActiveAnnouncement(id);

        existing.setTitle(request.getTitle().trim());
        existing.setContent(request.getContent().trim());
        existing.setPriority(request.getPriority());
        existing.setAudiences(new HashSet<>(request.getAudiences()));
        existing.setPinned(request.isPinned());
        if (request.getPublishedAt() != null)
            existing.setPublishedAt(request.getPublishedAt());

        Announcement updated = repository.save(existing);

        return ResponseEntity.ok(ApiResponse.<AnnouncementResponse>builder()
                .success(true)
                .message("Announcement updated successfully")
                .data(AnnouncementMapper.toResponse(updated))
                .build());
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponse<AnnouncementResponse>> changeStatus(UUID id, AnnouncementChangeStatusRequest request) {
        Announcement announcement = findActiveAnnouncement(id);

        if (announcement.getStatus() == request.getStatus())
            throw new CustomConflictException("Announcement is already in status: " + request.getStatus());

        LocalDateTime publishedAt = resolvePublishedAt(request.getStatus(), null, announcement.getPublishedAt());
        validateScheduled(request.getStatus(), publishedAt);

        announcement.setStatus(request.getStatus());
        announcement.setPublishedAt(publishedAt);

        Announcement updated = repository.save(announcement);

        return ResponseEntity.ok(ApiResponse.<AnnouncementResponse>builder()
                .success(true)
                .message("Announcement status changed successfully")
                .data(AnnouncementMapper.toResponse(updated))
                .build());
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponse<AnnouncementResponse>> changePin(UUID id, AnnouncementPinRequest request) {
        Announcement announcement = findActiveAnnouncement(id);

        announcement.setPinned(Boolean.TRUE.equals(request.getPinned()));
        Announcement updated = repository.save(announcement);

        return ResponseEntity.ok(ApiResponse.<AnnouncementResponse>builder()
                .success(true)
                .message(updated.isPinned() ? "Announcement pinned successfully" : "Announcement unpinned successfully")
                .data(AnnouncementMapper.toResponse(updated))
                .build());
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponse<AnnouncementResponse>> recordView(UUID id) {
        Announcement announcement = findActiveAnnouncement(id);

        announcement.setViewCount(announcement.getViewCount() + 1);
        Announcement updated = repository.save(announcement);

        return ResponseEntity.ok(ApiResponse.<AnnouncementResponse>builder()
                .success(true)
                .message("Announcement view recorded successfully")
                .data(AnnouncementMapper.toResponse(updated))
                .build());
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponse<Void>> deleteAnnouncement(UUID id) {
        Announcement announcement = findActiveAnnouncement(id);

        announcement.setDeleted(true);
        announcement.setDeletedAt(LocalDateTime.now());
        repository.save(announcement);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Announcement deleted successfully")
                .build());
    }

    @Override
    @Transactional
    public int publishDueScheduledAnnouncements() {
        List<Announcement> due = repository.findByStatusAndDeletedFalseAndPublishedAtLessThanEqual(
                AnnouncementStatus.SCHEDULED, LocalDateTime.now());

        if (due.isEmpty())
            return 0;

        due.forEach(announcement -> announcement.setStatus(AnnouncementStatus.PUBLISHED));
        repository.saveAll(due);

        return due.size();
    }

    private Announcement findActiveAnnouncement(UUID id) {
        return repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new CustomNotFoundException("Announcement not found with id: " + id));
    }

    private LocalDateTime resolvePublishedAt(AnnouncementStatus status, LocalDateTime requested, LocalDateTime current) {
        return switch (status) {
            case PUBLISHED -> {
                if (current != null) yield current;
                yield requested != null ? requested : LocalDateTime.now();
            }
            case SCHEDULED -> requested != null ? requested : current;
            default -> current;
        };
    }

    private void validateScheduled(AnnouncementStatus status, LocalDateTime publishedAt) {
        if (status != AnnouncementStatus.SCHEDULED)
            return;

        if (publishedAt == null)
            throw new CustomBadRequestException("publishedAt is required to schedule an announcement");

        if (!publishedAt.isAfter(LocalDateTime.now()))
            throw new CustomBadRequestException("publishedAt must be in the future to schedule an announcement");
    }
}
