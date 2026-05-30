package uz.mirmaxsudov.lmsbackend.repository.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.mirmaxsudov.lmsbackend.model.entity.auth.Role;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {
    Optional<Role> findByName(@Param("roleName") String roleName);

    @Query("""
            select distinct r
            from User u
            join u.roles r
            where u.id = :userId
            """)
    Set<Role> findAllWithPermissionByUserId(@Param("userId") UUID userId);
}
