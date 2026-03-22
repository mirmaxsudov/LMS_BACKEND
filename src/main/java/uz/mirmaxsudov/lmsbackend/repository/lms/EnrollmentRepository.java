package uz.mirmaxsudov.lmsbackend.repository.lms;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.Enrollment;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.EnrollmentStatus;

import java.util.UUID;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, UUID>, JpaSpecificationExecutor<Enrollment> {
    boolean existsByGroupIdAndStudentProfileId(UUID groupId, UUID studentProfileId);

    long countByGroupIdAndStatus(UUID groupId, EnrollmentStatus status);
}
