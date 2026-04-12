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

    @Query("""
            select distinct u
            from User u
            left join fetch u.roles r
            left join fetch r.permissions
            where u.email = :email
            """)
    Optional<User> findByEmailWithAuthorities(@Param("email") String email);
}
