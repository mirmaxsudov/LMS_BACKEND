package uz.mirmaxsudov.lmsbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.mirmaxsudov.lmsbackend.model.entity.auth.User;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {
    Optional<User> findByEmail(@Param("email") String email);

    Optional<User> findByIdAndDeletedFalse(UUID id);

    @Query("""
            select distinct u
            from User u
            left join fetch u.roles r
            left join fetch r.permissions
            where u.email = :email
              and u.deleted = false
            """)
    Optional<User> findByEmailWithAuthorities(@Param("email") String email);

    @Query("""
            select distinct u
            from User u
            left join fetch u.roles r
            left join fetch r.permissions
            where u.id = :id
              and u.deleted = false
            """)
    Optional<User> findByIdWithRolesAndPermissions(@Param("id") UUID id);

    @Query("""
            select count(distinct u)
            from User u
            join u.roles r
            where u.deleted = false
              and r.deleted = false
              and upper(r.name) = upper(:roleName)
            """)
    long countActiveUsersByRoleName(@Param("roleName") String roleName);
}
