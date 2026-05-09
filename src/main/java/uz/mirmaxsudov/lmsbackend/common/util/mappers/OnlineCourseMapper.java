package uz.mirmaxsudov.lmsbackend.common.util.mappers;

import uz.mirmaxsudov.lmsbackend.model.entity.auth.User;
import uz.mirmaxsudov.lmsbackend.model.entity.content.Attachment;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.online.OnlineCourse;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.online.OnlineCourseEnrollment;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.online.OnlineCourseLesson;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.online.OnlineCourseLessonMaterial;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.online.OnlineCourseLessonProgress;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.online.OnlineCourseModule;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.online.OnlineCourseModuleProgress;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.online.OnlineCourseContentStatus;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.online.OnlineCourseProgressStatus;
import uz.mirmaxsudov.lmsbackend.model.response.lms.online.OnlineCourseEnrollmentResponse;
import uz.mirmaxsudov.lmsbackend.model.response.lms.online.OnlineCourseLessonMaterialResponse;
import uz.mirmaxsudov.lmsbackend.model.response.lms.online.OnlineCourseLessonResponse;
import uz.mirmaxsudov.lmsbackend.model.response.lms.online.OnlineCourseModuleResponse;
import uz.mirmaxsudov.lmsbackend.model.response.lms.online.OnlineCourseProgressResponse;
import uz.mirmaxsudov.lmsbackend.model.response.lms.online.OnlineCourseResponse;
import uz.mirmaxsudov.lmsbackend.model.response.lms.online.OnlineCourseSummaryResponse;

import java.util.Comparator;
import java.util.List;

public final class OnlineCourseMapper {
    private OnlineCourseMapper() {
    }

    public static OnlineCourseSummaryResponse toSummary(OnlineCourse course) {
        if (course == null)
            return null;

        Attachment thumbnail = course.getThumbnail();
        return OnlineCourseSummaryResponse.builder()
                .id(course.getId())
                .title(course.getTitle())
                .slug(course.getSlug())
                .shortDescription(course.getShortDescription())
                .level(course.getLevel())
                .status(course.getStatus())
                .unlockStrategy(course.getUnlockStrategy())
                .estimatedDurationInMinutes(course.getEstimatedDurationInMinutes())
                .thumbnailId(thumbnail == null ? null : thumbnail.getId())
                .thumbnailUrl(thumbnail == null ? null : thumbnail.getUrl())
                .build();
    }

    public static OnlineCourseResponse toResponse(OnlineCourse course, boolean includeDraftContent) {
        if (course == null)
            return null;

        Attachment thumbnail = course.getThumbnail();
        OnlineCourseResponse response = OnlineCourseResponse.builder()
                .id(course.getId())
                .title(course.getTitle())
                .slug(course.getSlug())
                .shortDescription(course.getShortDescription())
                .description(course.getDescription())
                .level(course.getLevel())
                .status(course.getStatus())
                .unlockStrategy(course.getUnlockStrategy())
                .estimatedDurationInMinutes(course.getEstimatedDurationInMinutes())
                .thumbnailId(thumbnail == null ? null : thumbnail.getId())
                .thumbnailUrl(thumbnail == null ? null : thumbnail.getUrl())
                .createdById(course.getCreatedBy() == null ? null : course.getCreatedBy().getId())
                .createdByName(fullName(course.getCreatedBy()))
                .build();

        if (course.getModules() != null) {
            response.setModules(course.getModules().stream()
                    .filter(module -> isVisible(module, includeDraftContent))
                    .sorted(Comparator.comparing(OnlineCourseModule::getOrderIndex))
                    .map(module -> toModuleResponse(module, includeDraftContent))
                    .toList());
        } else {
            response.setModules(List.of());
        }

        return response;
    }

    public static OnlineCourseModuleResponse toModuleResponse(OnlineCourseModule module, boolean includeDraftContent) {
        if (module == null)
            return null;

        OnlineCourseModuleResponse response = OnlineCourseModuleResponse.builder()
                .id(module.getId())
                .courseId(module.getCourse() == null ? null : module.getCourse().getId())
                .title(module.getTitle())
                .description(module.getDescription())
                .orderIndex(module.getOrderIndex())
                .status(module.getStatus())
                .availableFrom(module.getAvailableFrom())
                .build();

        if (module.getLessons() != null) {
            response.setLessons(module.getLessons().stream()
                    .filter(lesson -> isVisible(lesson, includeDraftContent))
                    .sorted(Comparator.comparing(OnlineCourseLesson::getOrderIndex))
                    .map(lesson -> toLessonResponse(lesson, includeDraftContent))
                    .toList());
        } else {
            response.setLessons(List.of());
        }

        return response;
    }

    public static OnlineCourseLessonResponse toLessonResponse(OnlineCourseLesson lesson, boolean includeDraftContent) {
        if (lesson == null)
            return null;

        Attachment video = lesson.getVideoAttachment();
        OnlineCourseLessonResponse response = OnlineCourseLessonResponse.builder()
                .id(lesson.getId())
                .moduleId(lesson.getModule() == null ? null : lesson.getModule().getId())
                .title(lesson.getTitle())
                .description(lesson.getDescription())
                .content(lesson.getContent())
                .orderIndex(lesson.getOrderIndex())
                .durationInMinutes(lesson.getDurationInMinutes())
                .freePreview(lesson.isFreePreview())
                .status(lesson.getStatus())
                .availableFrom(lesson.getAvailableFrom())
                .videoAttachmentId(video == null ? null : video.getId())
                .videoUrl(video == null ? null : video.getUrl())
                .build();

        if (lesson.getMaterials() != null) {
            response.setMaterials(lesson.getMaterials().stream()
                    .filter(material -> !material.isDeleted())
                    .sorted(Comparator.comparing(OnlineCourseLessonMaterial::getOrderIndex))
                    .map(OnlineCourseMapper::toMaterialResponse)
                    .toList());
        } else {
            response.setMaterials(List.of());
        }

        return response;
    }

    public static OnlineCourseLessonMaterialResponse toMaterialResponse(OnlineCourseLessonMaterial material) {
        if (material == null)
            return null;

        Attachment attachment = material.getAttachment();
        return OnlineCourseLessonMaterialResponse.builder()
                .id(material.getId())
                .lessonId(material.getLesson() == null ? null : material.getLesson().getId())
                .attachmentId(attachment == null ? null : attachment.getId())
                .attachmentUrl(attachment == null ? null : attachment.getUrl())
                .title(material.getTitle())
                .orderIndex(material.getOrderIndex())
                .build();
    }

    public static OnlineCourseEnrollmentResponse toEnrollmentResponse(OnlineCourseEnrollment enrollment) {
        if (enrollment == null)
            return null;

        List<OnlineCourseLessonProgress> lessonProgresses = enrollment.getLessonProgresses() == null
                ? List.of()
                : enrollment.getLessonProgresses().stream()
                .filter(progress -> !progress.isDeleted())
                .toList();

        List<OnlineCourseModuleProgress> moduleProgresses = enrollment.getModuleProgresses() == null
                ? List.of()
                : enrollment.getModuleProgresses().stream()
                .filter(progress -> !progress.isDeleted())
                .toList();

        int totalLessons = lessonProgresses.size();
        int completedLessons = (int) lessonProgresses.stream()
                .filter(progress -> OnlineCourseProgressStatus.COMPLETED.equals(progress.getStatus()))
                .count();
        double progressPercentage = totalLessons == 0 ? 0 : (completedLessons * 100.0) / totalLessons;

        return OnlineCourseEnrollmentResponse.builder()
                .id(enrollment.getId())
                .course(toSummary(enrollment.getCourse()))
                .studentProfileId(enrollment.getStudent() == null ? null : enrollment.getStudent().getId())
                .studentUserId(enrollment.getStudent() == null ? null : enrollment.getStudent().getUser().getId())
                .studentName(enrollment.getStudent() == null ? null : fullName(enrollment.getStudent().getUser()))
                .openedById(enrollment.getOpenedBy() == null ? null : enrollment.getOpenedBy().getId())
                .status(enrollment.getStatus())
                .openedAt(enrollment.getOpenedAt())
                .completedAt(enrollment.getCompletedAt())
                .currentModuleId(enrollment.getCurrentModule() == null ? null : enrollment.getCurrentModule().getId())
                .currentLessonId(enrollment.getCurrentLesson() == null ? null : enrollment.getCurrentLesson().getId())
                .completedLessons(completedLessons)
                .totalLessons(totalLessons)
                .progressPercentage(progressPercentage)
                .moduleProgresses(moduleProgresses.stream()
                        .map(OnlineCourseMapper::toModuleProgressResponse)
                        .toList())
                .lessonProgresses(lessonProgresses.stream()
                        .map(OnlineCourseMapper::toLessonProgressResponse)
                        .toList())
                .build();
    }

    private static OnlineCourseProgressResponse toModuleProgressResponse(OnlineCourseModuleProgress progress) {
        return OnlineCourseProgressResponse.builder()
                .id(progress.getId())
                .enrollmentId(progress.getEnrollment() == null ? null : progress.getEnrollment().getId())
                .contentId(progress.getModule() == null ? null : progress.getModule().getId())
                .title(progress.getModule() == null ? null : progress.getModule().getTitle())
                .status(progress.getStatus())
                .openedAt(progress.getOpenedAt())
                .startedAt(progress.getStartedAt())
                .completedAt(progress.getCompletedAt())
                .build();
    }

    private static OnlineCourseProgressResponse toLessonProgressResponse(OnlineCourseLessonProgress progress) {
        return OnlineCourseProgressResponse.builder()
                .id(progress.getId())
                .enrollmentId(progress.getEnrollment() == null ? null : progress.getEnrollment().getId())
                .contentId(progress.getLesson() == null ? null : progress.getLesson().getId())
                .title(progress.getLesson() == null ? null : progress.getLesson().getTitle())
                .status(progress.getStatus())
                .lastPositionInSeconds(progress.getLastPositionInSeconds())
                .openedAt(progress.getOpenedAt())
                .startedAt(progress.getStartedAt())
                .completedAt(progress.getCompletedAt())
                .build();
    }

    private static boolean isVisible(OnlineCourseModule module, boolean includeDraftContent) {
        return !module.isDeleted()
                && (includeDraftContent || OnlineCourseContentStatus.PUBLISHED.equals(module.getStatus()));
    }

    private static boolean isVisible(OnlineCourseLesson lesson, boolean includeDraftContent) {
        return !lesson.isDeleted()
                && (includeDraftContent || OnlineCourseContentStatus.PUBLISHED.equals(lesson.getStatus()));
    }

    private static String fullName(User user) {
        if (user == null)
            return null;

        return (user.getFirstName() + " " + user.getLastName()).trim();
    }
}
