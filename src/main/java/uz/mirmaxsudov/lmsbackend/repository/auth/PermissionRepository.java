package uz.mirmaxsudov.lmsbackend.repository.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.mirmaxsudov.lmsbackend.model.entity.auth.Permission;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, UUID> {
    Optional<Permission> findByCode(@Param("code") String code);
}
