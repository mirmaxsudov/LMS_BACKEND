package uz.mirmaxsudov.lmsbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.mirmaxsudov.lmsbackend.model.entity.auth.User;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(@Param("email") String email);
}