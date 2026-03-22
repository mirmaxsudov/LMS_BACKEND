package uz.mirmaxsudov.lmsbackend.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import uz.mirmaxsudov.lmsbackend.model.entity.user.ParentProfile;

import java.util.UUID;

@Repository
public interface ParentProfileRepository extends JpaRepository<ParentProfile, UUID>, JpaSpecificationExecutor<ParentProfile> {
}
