package uz.mirmaxsudov.lmsbackend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.mirmaxsudov.lmsbackend.model.entity.auth.User;
import uz.mirmaxsudov.lmsbackend.repository.UserRepository;
import uz.mirmaxsudov.lmsbackend.service.base.UserService;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}