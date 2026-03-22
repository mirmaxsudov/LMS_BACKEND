package uz.mirmaxsudov.lmsbackend.repository.lms;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.Lecture;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface LectureRepository extends JpaRepository<Lecture, UUID> {
    List<Lecture> findBySemesterId(UUID semesterId);

    List<Lecture> findByTeacherId(UUID teacherId);

    List<Lecture> findByStartsAtBetween(LocalDateTime startsAt, LocalDateTime endsAt);
}
