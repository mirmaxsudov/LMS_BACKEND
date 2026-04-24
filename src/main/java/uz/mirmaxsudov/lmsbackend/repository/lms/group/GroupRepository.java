package uz.mirmaxsudov.lmsbackend.repository.lms.group;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.Group;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface GroupRepository extends JpaRepository<Group, UUID>, JpaSpecificationExecutor<Group> {
    Optional<Group> findByIdAndDeletedFalse(UUID id);

    boolean existsByGroupNameIgnoreCaseAndCourseIdAndDeletedFalse(String groupName, UUID courseId);

    boolean existsByGroupNameIgnoreCaseAndCourseIdAndIdNotAndDeletedFalse(String groupName, UUID courseId, UUID id);
}
