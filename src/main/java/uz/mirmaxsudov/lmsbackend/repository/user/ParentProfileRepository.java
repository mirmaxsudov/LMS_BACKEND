package uz.mirmaxsudov.lmsbackend.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.mirmaxsudov.lmsbackend.model.entity.user.ParentProfile;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ParentProfileRepository extends JpaRepository<ParentProfile, UUID>, JpaSpecificationExecutor<ParentProfile> {
    Optional<ParentProfile> findByUserId(UUID userId);

    @Query("""
            select count(p) > 0
            from ParentProfile p
            join p.students s
            where p.user.id = :parentUserId
              and s.id = :studentProfileId
              and p.deleted = false
              and s.deleted = false
            """)
    boolean existsActiveStudentLink(
            @Param("parentUserId") UUID parentUserId,
            @Param("studentProfileId") UUID studentProfileId
    );
}
