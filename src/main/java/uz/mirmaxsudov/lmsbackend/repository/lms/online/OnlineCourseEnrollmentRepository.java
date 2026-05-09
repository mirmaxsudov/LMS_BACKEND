package uz.mirmaxsudov.lmsbackend.repository.lms.online;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.online.OnlineCourseEnrollment;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OnlineCourseEnrollmentRepository extends JpaRepository<OnlineCourseEnrollment, UUID>, JpaSpecificationExecutor<OnlineCourseEnrollment> {
    Optional<OnlineCourseEnrollment> findByIdAndDeletedFalse(UUID id);

    Optional<OnlineCourseEnrollment> findByCourseIdAndStudentIdAndDeletedFalse(UUID courseId, UUID studentProfileId);
}
