//package uz.mirmaxsudov.lmsbackend.runner;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Component;
//import uz.mirmaxsudov.lmsbackend.model.entity.auth.Role;
//import uz.mirmaxsudov.lmsbackend.model.entity.auth.User;
//import uz.mirmaxsudov.lmsbackend.model.enums.Gender;
//import uz.mirmaxsudov.lmsbackend.model.enums.auth.SystemRole;
//import uz.mirmaxsudov.lmsbackend.model.enums.auth.UserStatus;
//import uz.mirmaxsudov.lmsbackend.repository.UserRepository;
//import uz.mirmaxsudov.lmsbackend.repository.auth.RoleRepository;
//
//import java.util.HashSet;
//
//@Component
//@RequiredArgsConstructor
//public class UserInitializer implements CommandLineRunner {
//    private static final String SUPER_ADMIN_EMAIL = "abdurahmonmirmaxsudov2804@gmail.com";
//    private static final String SUPER_ADMIN_PASSWORD = "12345678";
//
//    private final UserRepository userRepository;
//    private final RoleRepository roleRepository;
//    private final PasswordEncoder passwordEncoder;
//
//    @Override
//    public void run(String... args) {
//        Role superAdminRole = roleRepository.findByName(SystemRole.SUPER_ADMIN.name())
//                .orElseGet(() -> {
//                    Role role = new Role();
//                    role.setName(SystemRole.SUPER_ADMIN.name());
//                    role.setDescription("System super administrator");
//                    return roleRepository.save(role);
//                });
//
//        User user = userRepository.findByEmail(SUPER_ADMIN_EMAIL)
//                .orElseGet(() -> User.builder()
//                        .email(SUPER_ADMIN_EMAIL)
//                        .status(UserStatus.ACTIVE)
//                        .firstName("Abdurahmon")
//                        .lastName("Mirmaxsudov")
//                        .middleName("MirBahodir")
//                        .password(passwordEncoder.encode(SUPER_ADMIN_PASSWORD))
//                        .roles(new HashSet<>())
//                        .build());
//
//        if (user.getRoles().add(superAdminRole)) {
//            userRepository.save(user);
//            return;
//        }
//
//        if (user.getStatus() == null) {
//            user.setStatus(UserStatus.ACTIVE);
//            userRepository.save(user);
//        }
//    }
//}
