package uz.mirmaxsudov.lmsbackend.repository.lms.announcement;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.Announcement;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.AnnouncementStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, UUID>, JpaSpecificationExecutor<Announcement> {
    Optional<Announcement> findByIdAndDeletedFalse(UUID id);

    List<Announcement> findByStatusAndDeletedFalseAndPublishedAtLessThanEqual(
            AnnouncementStatus status,
            LocalDateTime threshold
    );

    long countByStatusAndDeletedFalse(AnnouncementStatus status);

    long countByPinnedTrueAndDeletedFalse();

    @Query("SELECT COALESCE(SUM(a.viewCount), 0) FROM Announcement a " +
            "WHERE a.status = :status AND a.deleted = false")
    long sumViewCountByStatus(@Param("status") AnnouncementStatus status);
}
