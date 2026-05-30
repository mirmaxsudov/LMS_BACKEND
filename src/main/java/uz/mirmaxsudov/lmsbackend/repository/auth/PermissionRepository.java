package uz.mirmaxsudov.lmsbackend.repository.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.mirmaxsudov.lmsbackend.model.entity.auth.Permission;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, UUID>, JpaSpecificationExecutor<Permission> {
    Optional<Permission> findByCode(@Param("code") String code);

    Optional<Permission> findByIdAndDeletedFalse(UUID id);

    boolean existsByCodeIgnoreCaseAndDeletedFalse(String code);

    boolean existsByCodeIgnoreCaseAndIdNotAndDeletedFalse(String code, UUID id);

    List<Permission> findAllByIdInAndDeletedFalse(Collection<UUID> ids);

    @Query("""
            select distinct p
            from User u
            join u.roles r
            join r.permissions p
            where u.id = :userId
              and u.deleted = false
              and r.deleted = false
              and p.deleted = false
            """)
    Set<Permission> getAllByUserId(@Param("userId") UUID userId);
}
