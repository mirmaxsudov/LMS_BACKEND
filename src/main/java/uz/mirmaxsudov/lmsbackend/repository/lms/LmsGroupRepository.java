package uz.mirmaxsudov.lmsbackend.repository.lms;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.LmsGroup;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LmsGroupRepository extends JpaRepository<LmsGroup, UUID>, JpaSpecificationExecutor<LmsGroup> {
    Optional<LmsGroup> findByIdAndTeacherId(UUID id, UUID teacherId);
}
