package uz.mirmaxsudov.lmsbackend.service.base;

import uz.mirmaxsudov.lmsbackend.model.entity.auth.User;

import java.util.Optional;

public interface UserService {
    Optional<User> getUserByEmail(String email);
}
