package uz.mirmaxsudov.lmsbackend.common.util.mappers;

import uz.mirmaxsudov.lmsbackend.model.entity.auth.Role;
import uz.mirmaxsudov.lmsbackend.model.entity.auth.User;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.Announcement;
import uz.mirmaxsudov.lmsbackend.model.response.lms.AnnouncementResponse;

import java.util.HashSet;

public final class AnnouncementMapper {

    private AnnouncementMapper() {
    }

    public static AnnouncementResponse toResponse(Announcement announcement) {
        if (announcement == null) return null;

        User author = announcement.getAuthor();

        return AnnouncementResponse.builder()
                .id(announcement.getId())
                .title(announcement.getTitle())
                .content(announcement.getContent())
                .status(announcement.getStatus())
                .priority(announcement.getPriority())
                .audiences(announcement.getAudiences() == null
                        ? null
                        : new HashSet<>(announcement.getAudiences()))
                .pinned(announcement.isPinned())
                .viewCount(announcement.getViewCount())
                .publishedAt(announcement.getPublishedAt())
                .authorId(author != null ? author.getId() : null)
                .authorName(resolveAuthorName(author))
                .authorRole(resolveAuthorRole(author))
                .createdAt(announcement.getCreatedAt())
                .updatedAt(announcement.getUpdatedAt())
                .build();
    }

    private static String resolveAuthorName(User author) {
        if (author == null) return null;

        String firstName = author.getFirstName() != null ? author.getFirstName() : "";
        String lastName = author.getLastName() != null ? author.getLastName() : "";
        String fullName = (firstName + " " + lastName).trim();

        return fullName.isBlank() ? null : fullName;
    }

    private static String resolveAuthorRole(User author) {
        if (author == null || author.getRoles() == null) return null;

        return author.getRoles().stream()
                .map(Role::getName)
                .findFirst()
                .orElse(null);
    }
}
