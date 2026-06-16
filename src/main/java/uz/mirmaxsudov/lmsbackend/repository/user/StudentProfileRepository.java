package uz.mirmaxsudov.lmsbackend.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.mirmaxsudov.lmsbackend.model.entity.user.StudentProfile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StudentProfileRepository extends JpaRepository<StudentProfile, UUID>, JpaSpecificationExecutor<StudentProfile> {
    Optional<StudentProfile> findByUserId(UUID userId);

    Optional<StudentProfile> findByIdAndDeletedFalse(UUID id);

    List<StudentProfile> findAllByStudentIdInAndDeletedFalse(List<UUID> ids);

    List<StudentProfile> findAllByGroups_IdAndDeletedFalse(UUID groupId);

    @Query("""
            select count(s) > 0 from StudentProfile s
            join s.groups g
            where s.id = :studentId
              and g.id = :groupId
              and s.deleted = false
              and g.deleted = false
            """)
    boolean existsActiveStudentInGroup(@Param("studentId") UUID studentId, @Param("groupId") UUID groupId);

    @Query("""
            select count(distinct sp) from StudentProfile sp
            join sp.groups g
            join g.students s
            where s.id = :studentId
              and sp.id <> :studentId
              and sp.deleted = false
              and g.deleted = false
            """)
    long countClassmates(@Param("studentId") UUID studentId);
}
