package uz.mirmaxsudov.lmsbackend.repository.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.mirmaxsudov.lmsbackend.model.entity.auth.Role;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID>, JpaSpecificationExecutor<Role> {
    Optional<Role> findByName(@Param("roleName") String roleName);

    Optional<Role> findByIdAndDeletedFalse(UUID id);

    boolean existsByNameIgnoreCaseAndDeletedFalse(String name);

    boolean existsByNameIgnoreCaseAndIdNotAndDeletedFalse(String name, UUID id);

    List<Role> findAllByIdInAndDeletedFalse(Collection<UUID> ids);

    @Query("""
            select distinct r
            from Role r
            left join fetch r.permissions
            where r.id = :id
              and r.deleted = false
            """)
    Optional<Role> findWithPermissionsByIdAndDeletedFalse(@Param("id") UUID id);

    @Query("""
            select distinct r
            from Role r
            left join fetch r.permissions
            where r.id in :ids
              and r.deleted = false
            """)
    Set<Role> findAllWithPermissionsByIdInAndDeletedFalse(@Param("ids") Collection<UUID> ids);

    @Query("""
            select distinct r
            from User u
            join u.roles r
            where u.id = :userId
              and u.deleted = false
              and r.deleted = false
            """)
    Set<Role> findAllWithPermissionByUserId(@Param("userId") UUID userId);

    @Query("""
            select count(u) > 0
            from User u
            join u.roles r
            where r.id = :roleId
              and u.deleted = false
            """)
    boolean existsAssignedToActiveUser(@Param("roleId") UUID roleId);

    @Query("""
            select count(r) > 0
            from Role r
            join r.permissions p
            where p.id = :permissionId
              and r.deleted = false
            """)
    boolean existsActiveRoleWithPermission(@Param("permissionId") UUID permissionId);
}
