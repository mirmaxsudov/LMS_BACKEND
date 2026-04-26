//package uz.mirmaxsudov.lmsbackend.runner;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//import uz.mirmaxsudov.lmsbackend.model.entity.auth.User;
//import uz.mirmaxsudov.lmsbackend.model.entity.lms.Course;
//import uz.mirmaxsudov.lmsbackend.model.entity.lms.Group;
//import uz.mirmaxsudov.lmsbackend.model.entity.user.TeacherProfile;
//import uz.mirmaxsudov.lmsbackend.model.enums.auth.UserStatus;
//import uz.mirmaxsudov.lmsbackend.model.enums.lms.CourseLevel;
//import uz.mirmaxsudov.lmsbackend.model.enums.lms.GroupStatus;
//import uz.mirmaxsudov.lmsbackend.model.enums.lms.TeacherPosition;
//import uz.mirmaxsudov.lmsbackend.repository.UserRepository;
//import uz.mirmaxsudov.lmsbackend.repository.lms.course.CourseRepository;
//import uz.mirmaxsudov.lmsbackend.repository.lms.group.GroupRepository;
//import uz.mirmaxsudov.lmsbackend.repository.user.TeacherProfileRepository;
//
//import java.util.List;
//
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class GroupInitializer implements CommandLineRunner {
//
//    private final GroupRepository groupRepository;
//    private final CourseRepository courseRepository;
//    private final TeacherProfileRepository teacherProfileRepository;
//    private final UserRepository userRepository;
//
//    @Override
//    @Transactional
//    public void run(String... args) {
//        if (groupRepository.count() >= 50) {
//            log.info("Groups already initialized (count: {}). Skipping group initialization.", groupRepository.count());
//            return;
//        }
//
//        log.info("Starting group initialization...");
//
//        Course course = courseRepository.findAll().stream()
//                .findFirst()
//                .orElseGet(() -> {
//                    log.info("No courses found. Creating a default course.");
//                    Course newCourse = Course.builder()
//                            .title("Default Course for Groups")
//                            .description("Auto-generated course for seeding groups")
//                            .level(CourseLevel.BEGINNER)
//                            .durationInMinutes(60)
//                            .build();
//                    return courseRepository.save(newCourse);
//                });
//
//        TeacherProfile teacher = teacherProfileRepository.findAll().stream()
//                .findFirst()
//                .orElseGet(() -> {
//                    log.info("No teachers found. Creating a default teacher.");
//                    User user = User.builder()
//                            .firstName("Default")
//                            .lastName("Teacher")
//                            .email("default.teacher@lms.uz")
//                            .password("password")
//                            .status(UserStatus.ACTIVE)
//                            .build();
//                    user = userRepository.save(user);
//
//                    TeacherProfile profile = TeacherProfile.builder()
//                            .user(user)
//                            .position(TeacherPosition.LECTURER)
//                            .build();
//                    return teacherProfileRepository.save(profile);
//                });
//
//        long existingCount = groupRepository.count();
//        int toCreate = 50 - (int) existingCount;
//
//        for (int i = 1; i <= toCreate; i++) {
//            String groupName = "Group " + (existingCount + i);
//            Group group = Group.builder()
//                    .groupName(groupName)
//                    .course(course)
//                    .teacher(teacher)
//                    .status(GroupStatus.FORMING)
//                    .capacity(20)
//                    .build();
//            groupRepository.save(group);
//        }
//
//        log.info("Successfully initialized {} groups.", toCreate);
//    }
//}
