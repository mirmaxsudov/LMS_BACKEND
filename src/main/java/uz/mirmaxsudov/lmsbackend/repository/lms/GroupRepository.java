package uz.mirmaxsudov.lmsbackend.repository.lms;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.Group;

import java.util.UUID;

public interface GroupRepository extends JpaRepository<Group, UUID> {
}
