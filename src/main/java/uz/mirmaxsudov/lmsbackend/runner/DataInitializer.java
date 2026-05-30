//package uz.mirmaxsudov.lmsbackend.runner;
//
//import jakarta.transaction.Transactional;
//import lombok.RequiredArgsConstructor;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//import uz.mirmaxsudov.lmsbackend.model.entity.auth.Permission;
//import uz.mirmaxsudov.lmsbackend.model.entity.auth.Role;
//import uz.mirmaxsudov.lmsbackend.model.enums.auth.PermissionCategory;
//import uz.mirmaxsudov.lmsbackend.model.enums.auth.SystemPermission;
//import uz.mirmaxsudov.lmsbackend.model.enums.auth.SystemRole;
//import uz.mirmaxsudov.lmsbackend.repository.auth.PermissionRepository;
//import uz.mirmaxsudov.lmsbackend.repository.auth.RoleRepository;
//
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.Map;
//import java.util.Set;
//
//@Component
//@Transactional
//@RequiredArgsConstructor
//public class DataInitializer implements CommandLineRunner {
//    private final PermissionRepository permissionRepository;
//    private final RoleRepository roleRepository;
//
//    @Override
//    public void run(String... args) {
//        Map<String, Permission> permissionMap = seedPermissions();
//        seedRoles(permissionMap);
//    }
//
//    private Map<String, Permission> seedPermissions() {
//        Map<String, Permission> map = new HashMap<>();
//
//        for (SystemPermission permEnum : SystemPermission.values()) {
//            String code = permEnum.name();
//
//            Permission permission = permissionRepository
//                    .findByCode(code)
//                    .orElseGet(() -> {
//                        Permission p = new Permission();
//                        p.setCode(code);
//                        p.setIsSystem(true);
//                        p.setCategory(PermissionCategory.USER);
//                        return permissionRepository.save(p);
//                    });
//
//            map.put(code, permission);
//        }
//
//        return map;
//    }
//
//    private void seedRoles(Map<String, Permission> permissionMap) {
//        createRoleIfNotExists(
//                SystemRole.SUPER_ADMIN.name(),
//                new HashSet<>(permissionMap.values())
//        );
//
//        createRoleIfNotExists(
//                SystemRole.ADMIN.name(),
//                Set.of(
//                        permissionMap.get("USER_CREATE"),
//                        permissionMap.get("USER_EDIT"),
//                        permissionMap.get("COURSE_CREATE"),
//                        permissionMap.get("COURSE_EDIT"),
//                        permissionMap.get("COURSE_VIEW")
//                )
//        );
//
//        createRoleIfNotExists(
//                SystemRole.TEACHER.name(),
//                Set.of(
//                        permissionMap.get("COURSE_CREATE"),
//                        permissionMap.get("COURSE_EDIT"),
//                        permissionMap.get("QUIZ_CREATE"),
//                        permissionMap.get("QUIZ_EVALUATE")
//                )
//        );
//
//        createRoleIfNotExists(
//                SystemRole.STUDENT.name(),
//                Set.of(
//                        permissionMap.get("COURSE_VIEW"),
//                        permissionMap.get("QUIZ_START"),
//                        permissionMap.get("FILE_VIEW")
//                )
//        );
//    }
//
//    private void createRoleIfNotExists(String roleName,
//                                       Set<Permission> permissions) {
//
//        Role role = roleRepository.findByName(roleName)
//                .orElseGet(() -> {
//                    Role r = new Role();
//                    r.setName(roleName);
//                    return roleRepository.save(r);
//                });
//
//        role.setPermissions(new HashSet<>(permissions));
//        roleRepository.save(role);
//    }
//}
