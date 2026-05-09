//package uz.mirmaxsudov.lmsbackend.runner;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//import uz.mirmaxsudov.lmsbackend.model.entity.auth.Role;
//import uz.mirmaxsudov.lmsbackend.model.entity.auth.User;
//import uz.mirmaxsudov.lmsbackend.model.entity.lms.online.OnlineCourse;
//import uz.mirmaxsudov.lmsbackend.model.entity.lms.online.OnlineCourseLesson;
//import uz.mirmaxsudov.lmsbackend.model.entity.lms.online.OnlineCourseModule;
//import uz.mirmaxsudov.lmsbackend.model.enums.auth.SystemRole;
//import uz.mirmaxsudov.lmsbackend.model.enums.auth.UserStatus;
//import uz.mirmaxsudov.lmsbackend.model.enums.lms.CourseLevel;
//import uz.mirmaxsudov.lmsbackend.model.enums.lms.online.OnlineCourseContentStatus;
//import uz.mirmaxsudov.lmsbackend.model.enums.lms.online.OnlineCourseStatus;
//import uz.mirmaxsudov.lmsbackend.model.enums.lms.online.OnlineCourseUnlockStrategy;
//import uz.mirmaxsudov.lmsbackend.repository.UserRepository;
//import uz.mirmaxsudov.lmsbackend.repository.auth.RoleRepository;
//import uz.mirmaxsudov.lmsbackend.repository.lms.online.OnlineCourseRepository;
//
//import java.util.HashSet;
//import java.util.List;
//import java.util.stream.Stream;
//
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class OnlineCourseInitializer implements CommandLineRunner {
//    private static final String SEED_AUTHOR_EMAIL = "abdurahmonmirmaxsudov2804@gmail.com";
//    private static final String SEED_AUTHOR_PASSWORD = "12345678";
//
//    private final OnlineCourseRepository onlineCourseRepository;
//    private final UserRepository userRepository;
//    private final RoleRepository roleRepository;
//    private final PasswordEncoder passwordEncoder;
//
//    @Override
//    @Transactional
//    public void run(String... args) {
//        User author = getOrCreateSeedAuthor();
//        List<OnlineCourseSeed> seeds = buildSeeds();
//        int createdCount = 0;
//
//        for (OnlineCourseSeed seed : seeds) {
//            if (onlineCourseRepository.existsBySlug(seed.slug()))
//                continue;
//
//            onlineCourseRepository.save(toEntity(seed, author));
//            createdCount++;
//        }
//
//        log.info("Online course initializer created {} new courses.", createdCount);
//    }
//
//    private User getOrCreateSeedAuthor() {
//        Role maintainerRole = roleRepository.findByName(SystemRole.MAINTAINER.name())
//                .orElseGet(() -> roleRepository.save(Role.builder()
//                        .name(SystemRole.MAINTAINER.name())
//                        .description("Maintainer role for system seed data")
//                        .build()));
//
//        User author = userRepository.findByEmail(SEED_AUTHOR_EMAIL)
//                .orElseGet(() -> User.builder()
//                        .firstName("Online")
//                        .lastName("Course Seed")
//                        .email(SEED_AUTHOR_EMAIL)
//                        .password(passwordEncoder.encode(SEED_AUTHOR_PASSWORD))
//                        .status(UserStatus.ACTIVE)
//                        .roles(new HashSet<>())
//                        .build());
//
//        author.getRoles().add(maintainerRole);
//        return userRepository.save(author);
//    }
//
//    private OnlineCourse toEntity(OnlineCourseSeed seed, User author) {
//        OnlineCourse course = OnlineCourse.builder()
//                .title(seed.title())
//                .slug(seed.slug())
//                .shortDescription(seed.shortDescription())
//                .description(seed.description())
//                .level(seed.level())
//                .status(OnlineCourseStatus.PUBLISHED)
//                .unlockStrategy(seed.unlockStrategy())
//                .estimatedDurationInMinutes(seed.estimatedDurationInMinutes())
//                .createdBy(author)
//                .build();
//
//        for (int moduleIndex = 0; moduleIndex < seed.modules().size(); moduleIndex++) {
//            ModuleSeed moduleSeed = seed.modules().get(moduleIndex);
//            OnlineCourseModule module = OnlineCourseModule.builder()
//                    .course(course)
//                    .title(moduleSeed.title())
//                    .description(moduleSeed.description())
//                    .orderIndex(moduleIndex)
//                    .status(OnlineCourseContentStatus.PUBLISHED)
//                    .build();
//
//            for (int lessonIndex = 0; lessonIndex < moduleSeed.lessons().size(); lessonIndex++) {
//                LessonSeed lessonSeed = moduleSeed.lessons().get(lessonIndex);
//                OnlineCourseLesson lesson = OnlineCourseLesson.builder()
//                        .module(module)
//                        .title(lessonSeed.title())
//                        .description(lessonSeed.description())
//                        .content(lessonSeed.content())
//                        .orderIndex(lessonIndex)
//                        .durationInMinutes(lessonSeed.durationInMinutes())
//                        .freePreview(moduleIndex == 0 && lessonIndex == 0)
//                        .status(OnlineCourseContentStatus.PUBLISHED)
//                        .build();
//
//                module.getLessons().add(lesson);
//            }
//
//            course.getModules().add(module);
//        }
//
//        return course;
//    }
//
//    private List<OnlineCourseSeed> buildSeeds() {
//        return List.of(
//                course(
//                        "Java 21 Backend Foundations",
//                        "java-21-backend-foundations",
//                        CourseLevel.BEGINNER,
//                        OnlineCourseUnlockStrategy.LESSON_BY_LESSON,
//                        "Build a solid Java backend base with modern language features, DTOs, validation, and layered architecture.",
//                        module("Java and Project Structure",
//                                lesson("Modern Java syntax", "Records, switch expressions, text blocks, and pragmatic Java 21 usage.", 32),
//                                lesson("Backend package boundaries", "How controllers, services, repositories, DTOs, and mappers work together.", 28),
//                                lesson("Clean DTO validation", "Design request DTOs with Jakarta validation and service-level rules.", 34)),
//                        module("Service and Repository Design",
//                                lesson("Service contracts", "Use service interfaces to keep controllers thin and business logic focused.", 30),
//                                lesson("Spring Data repositories", "Create JPA repositories with active entity lookups and pagination.", 33),
//                                lesson("Soft delete patterns", "Exclude deleted rows and preserve audit-friendly deletion behavior.", 29)),
//                        module("Production Readiness",
//                                lesson("Error handling", "Return domain-specific errors with project exception types.", 25),
//                                lesson("Build verification", "Run Maven verification and interpret compiler warnings.", 24),
//                                lesson("Operational checklist", "Prepare endpoints for Swagger, Docker, and environment-driven configuration.", 31))
//                ),
//                course(
//                        "Spring Boot REST API Mastery",
//                        "spring-boot-rest-api-mastery",
//                        CourseLevel.INTERMEDIATE,
//                        OnlineCourseUnlockStrategy.MODULE_BY_MODULE,
//                        "Design predictable REST APIs with Spring Boot, response wrappers, pagination, filtering, and secure endpoints.",
//                        module("REST Resource Design",
//                                lesson("Resource-oriented URLs", "Model API paths around resources and nested ownership.", 27),
//                                lesson("Request and response wrappers", "Use ApiResponse and ApiPaginateResponse consistently.", 25),
//                                lesson("Pagination contracts", "Keep public pagination 1-based while repositories stay 0-based.", 22)),
//                        module("Filtering and Specifications",
//                                lesson("Specification basics", "Compose dynamic filters using Spring Data JPA Specification.", 31),
//                                lesson("Search filters", "Implement safe title, description, and enum filtering.", 26),
//                                lesson("Range filters", "Validate and apply duration or date range filters.", 24)),
//                        module("Controller Security",
//                                lesson("Role checks", "Apply method-level security with clear role boundaries.", 29),
//                                lesson("Principal-aware APIs", "Use AuthenticationPrincipal to scope data to the current user.", 26),
//                                lesson("Public endpoint rules", "Use OpenAuth only where anonymous access is intentional.", 21))
//                ),
//                course(
//                        "PostgreSQL and JPA for LMS Systems",
//                        "postgresql-and-jpa-for-lms-systems",
//                        CourseLevel.INTERMEDIATE,
//                        OnlineCourseUnlockStrategy.LESSON_BY_LESSON,
//                        "Use PostgreSQL and JPA mappings to build reliable LMS domain models with relationships and constraints.",
//                        module("Entity Modeling",
//                                lesson("BaseEntity audit fields", "Use UUID ids, timestamps, and soft-delete fields consistently.", 25),
//                                lesson("Many-to-one relationships", "Model ownership between courses, modules, lessons, and users.", 30),
//                                lesson("One-to-many cascades", "Understand cascade and orphan removal in aggregate-like structures.", 35)),
//                        module("Constraints and Indexes",
//                                lesson("Unique business keys", "Protect slugs and ordering with database constraints.", 27),
//                                lesson("Order indexes", "Keep course modules and lessons stable with indexed ordering.", 23),
//                                lesson("Lazy loading", "Map responses inside transaction-aware service methods.", 32)),
//                        module("Query Patterns",
//                                lesson("Active entity queries", "Use findByIdAndDeletedFalse and equivalent repository methods.", 20),
//                                lesson("Specification joins", "Filter enrollments by course, student, and status.", 28),
//                                lesson("Pagination performance", "Return bounded result sets with stable sort behavior.", 26))
//                ),
//                course(
//                        "Online Course Authoring Workflow",
//                        "online-course-authoring-workflow",
//                        CourseLevel.BEGINNER,
//                        OnlineCourseUnlockStrategy.ALL_AT_ONCE,
//                        "Create online courses from draft to published state with modules, lessons, materials, and previews.",
//                        module("Course Planning",
//                                lesson("Audience and outcomes", "Define course goals, target learners, and measurable outcomes.", 24),
//                                lesson("Module breakdown", "Split a course into progressive, focused modules.", 21),
//                                lesson("Lesson sequencing", "Use order indexes to keep learning paths clear.", 19)),
//                        module("Content Production",
//                                lesson("Lesson content", "Write concise lesson text that supports video and attachments.", 28),
//                                lesson("Free previews", "Choose introductory lessons that help students evaluate the course.", 18),
//                                lesson("Publication states", "Use draft, published, and hidden content states intentionally.", 26)),
//                        module("Maintenance",
//                                lesson("Updating published courses", "Revise course metadata without breaking enrolled students.", 22),
//                                lesson("Archiving strategy", "Archive obsolete courses while preserving historical enrollments.", 20),
//                                lesson("Quality review", "Check descriptions, duration, and learning continuity before release.", 25))
//                ),
//                course(
//                        "Secure LMS Authorization",
//                        "secure-lms-authorization",
//                        CourseLevel.ADVANCED,
//                        OnlineCourseUnlockStrategy.MODULE_BY_MODULE,
//                        "Build role-aware LMS flows for admins, teachers, students, parents, guardians, and maintainers.",
//                        module("Role Model",
//                                lesson("System roles", "Understand SUPER_ADMIN, ADMIN, TEACHER, STUDENT, PARENT, GUARDIAN, MAINTAINER, and SUPPORT_TEACHER.", 30),
//                                lesson("Authority mapping", "Convert roles and permissions into Spring Security authorities.", 27),
//                                lesson("Access boundaries", "Separate global management from ownership-based management.", 34)),
//                        module("Student Access",
//                                lesson("Published course access", "Allow students to browse only published online courses.", 25),
//                                lesson("Self enrollment", "Create enrollments for the authenticated student profile.", 29),
//                                lesson("Progress ownership", "Ensure students update only their own progress records.", 31)),
//                        module("Family Access",
//                                lesson("Parent and guardian views", "Expose linked student enrollment progress without write access.", 24),
//                                lesson("Relationship checks", "Validate parent-student links before returning progress.", 28),
//                                lesson("Forbidden responses", "Use access denied behavior for unauthorized profile access.", 22))
//                ),
//                course(
//                        "Resumable Video Uploads with TUS",
//                        "resumable-video-uploads-with-tus",
//                        CourseLevel.INTERMEDIATE,
//                        OnlineCourseUnlockStrategy.LESSON_BY_LESSON,
//                        "Implement large video upload flows with TUS, MinIO object storage, offsets, metadata, and resumability.",
//                        module("TUS Protocol Basics",
//                                lesson("Upload creation", "Create upload resources with Upload-Length and metadata headers.", 26),
//                                lesson("Chunk patching", "Append chunks with Upload-Offset and application/offset+octet-stream.", 33),
//                                lesson("Resume flow", "Use HEAD requests to recover the current upload offset.", 24)),
//                        module("Storage Integration",
//                                lesson("Chunk object keys", "Store temporary chunks under deterministic upload paths.", 27),
//                                lesson("Object composition", "Compose chunks into the final object when upload reaches full length.", 35),
//                                lesson("Cleanup strategy", "Remove chunks after completion when configured.", 19)),
//                        module("Course Video Usage",
//                                lesson("Video metadata", "Capture filename and content type in Upload-Metadata.", 22),
//                                lesson("Attachment registration", "Convert completed upload objects into attachment records before assigning lessons.", 31),
//                                lesson("Streaming reads", "Support range downloads for video playback.", 28))
//                ),
//                course(
//                        "Learning Progress Engine",
//                        "learning-progress-engine",
//                        CourseLevel.ADVANCED,
//                        OnlineCourseUnlockStrategy.LESSON_BY_LESSON,
//                        "Track online course progress, unlock content, and complete enrollments based on lesson state.",
//                        module("Enrollment Model",
//                                lesson("Enrollment lifecycle", "Move from active to completed while preserving opened metadata.", 24),
//                                lesson("Initial progress rows", "Create module and lesson progress records at enrollment time.", 31),
//                                lesson("Current position", "Track current module and lesson for resume experiences.", 20)),
//                        module("Unlock Strategies",
//                                lesson("All at once", "Make all published lessons available immediately.", 18),
//                                lesson("Module by module", "Unlock the next module after current module completion.", 28),
//                                lesson("Lesson by lesson", "Unlock the next lesson only after the current lesson is completed.", 32)),
//                        module("Completion Rules",
//                                lesson("Lesson progress updates", "Accept available, in-progress, and completed transitions.", 27),
//                                lesson("Module completion", "Mark modules complete when all their lessons are complete.", 26),
//                                lesson("Course completion", "Complete enrollment when every lesson progress is completed.", 25))
//                ),
//                course(
//                        "Clean Code for Spring Services",
//                        "clean-code-for-spring-services",
//                        CourseLevel.INTERMEDIATE,
//                        OnlineCourseUnlockStrategy.ALL_AT_ONCE,
//                        "Write Spring service classes with focused methods, explicit invariants, and maintainable role checks.",
//                        module("Service Boundaries",
//                                lesson("Thin controllers", "Keep HTTP details in controllers and business decisions in services.", 21),
//                                lesson("Private helper methods", "Use small helpers for repeated validation and normalization.", 26),
//                                lesson("Transactions", "Choose read-only and write transactions deliberately.", 23)),
//                        module("Validation",
//                                lesson("Structural validation", "Use Jakarta validation for DTO-level rules.", 18),
//                                lesson("State validation", "Check ownership, duplicate slugs, and invalid transitions in services.", 30),
//                                lesson("Exception choice", "Prefer custom API exceptions over raw runtime exceptions.", 24)),
//                        module("Maintainability",
//                                lesson("Naming", "Use precise names for domain concepts and operations.", 19),
//                                lesson("Mapper discipline", "Keep entity exposure out of REST responses.", 25),
//                                lesson("Scoped changes", "Avoid unrelated refactors when implementing feature work.", 17))
//                ),
//                course(
//                        "MinIO Storage for Backend Developers",
//                        "minio-storage-for-backend-developers",
//                        CourseLevel.BEGINNER,
//                        OnlineCourseUnlockStrategy.MODULE_BY_MODULE,
//                        "Use object storage safely for LMS attachments, downloadable resources, and video content.",
//                        module("Object Storage Concepts",
//                                lesson("Buckets and object keys", "Understand how object keys organize files without directories.", 22),
//                                lesson("Content types", "Preserve MIME type for images, documents, and videos.", 20),
//                                lesson("Public URLs", "Store API-safe URLs without leaking storage credentials.", 24)),
//                        module("Attachment Flows",
//                                lesson("Multipart upload", "Use the attachment API for smaller files and images.", 26),
//                                lesson("Large upload", "Use TUS for resumable large files such as videos.", 25),
//                                lesson("Delete behavior", "Remove storage objects when attachments are deleted.", 18)),
//                        module("Operational Concerns",
//                                lesson("Local MinIO", "Run MinIO through Docker Compose for development.", 19),
//                                lesson("Environment variables", "Configure endpoint, bucket, keys, and region externally.", 23),
//                                lesson("Failure handling", "Surface storage failures with clear API errors.", 21))
//                ),
//                course(
//                        "LMS Domain Modeling Practicum",
//                        "lms-domain-modeling-practicum",
//                        CourseLevel.ADVANCED,
//                        OnlineCourseUnlockStrategy.LESSON_BY_LESSON,
//                        "Practice designing LMS domain modules including courses, lessons, groups, attendance, and online learning.",
//                        module("Core LMS Concepts",
//                                lesson("Course vs online course", "Separate classroom course flows from self-paced online content.", 26),
//                                lesson("Groups and sessions", "Understand group schedules, lesson sessions, and attendance.", 29),
//                                lesson("Enrollments", "Model who can access what and under which status.", 24)),
//                        module("Online Learning Concepts",
//                                lesson("Modules", "Use modules as stable course sections for sequencing.", 20),
//                                lesson("Lessons", "Make lessons the unit of video, content, and progress.", 25),
//                                lesson("Materials", "Attach supporting files to lessons without coupling storage logic.", 22)),
//                        module("API Design Practicum",
//                                lesson("Nested create endpoints", "Create modules under courses and lessons under modules.", 27),
//                                lesson("Flat update endpoints", "Update existing child resources by id for simple routing.", 21),
//                                lesson("Progress endpoints", "Scope progress writes to the authenticated student.", 28))
//                ),
//                course(
//                        "Backend Verification and Operations",
//                        "backend-verification-and-operations",
//                        CourseLevel.INTERMEDIATE,
//                        OnlineCourseUnlockStrategy.ALL_AT_ONCE,
//                        "Verify Spring Boot backend changes with Maven, Docker services, logs, and operational checks.",
//                        module("Local Verification",
//                                lesson("Maven test", "Compile and run tests with the Maven wrapper.", 18),
//                                lesson("Clean package", "Run a full package build when JPA and configuration are touched.", 20),
//                                lesson("Build warnings", "Separate blocking failures from known warnings.", 17)),
//                        module("Runtime Services",
//                                lesson("PostgreSQL", "Start and verify the database service for local development.", 22),
//                                lesson("MinIO", "Start object storage and bucket initialization services.", 21),
//                                lesson("Swagger", "Inspect generated API docs after startup.", 16)),
//                        module("Troubleshooting",
//                                lesson("JAVA_HOME", "Set a valid JDK path before running Maven wrapper commands.", 19),
//                                lesson("Network dependency resolution", "Handle first-run dependency downloads cleanly.", 18),
//                                lesson("Security checks", "Verify whitelisted and authenticated endpoints separately.", 24))
//                )
//        );
//    }
//
//    private OnlineCourseSeed course(
//            String title,
//            String slug,
//            CourseLevel level,
//            OnlineCourseUnlockStrategy unlockStrategy,
//            String shortDescription,
//            ModuleSeed... modules
//    ) {
//        int totalDuration = Stream.of(modules)
//                .flatMap(module -> module.lessons().stream())
//                .mapToInt(LessonSeed::durationInMinutes)
//                .sum();
//
//        return new OnlineCourseSeed(
//                title,
//                slug,
//                shortDescription,
//                shortDescription + " This seeded course includes structured modules and detailed lessons for local development and QA.",
//                level,
//                unlockStrategy,
//                totalDuration,
//                List.of(modules)
//        );
//    }
//
//    private ModuleSeed module(String title, LessonSeed... lessons) {
//        return new ModuleSeed(
//                title,
//                "Detailed module covering " + title.toLowerCase() + " with practical LMS backend examples.",
//                List.of(lessons)
//        );
//    }
//
//    private LessonSeed lesson(String title, String content, int durationInMinutes) {
//        return new LessonSeed(
//                title,
//                content,
//                content + " The lesson includes practical checkpoints, implementation notes, and review prompts.",
//                durationInMinutes
//        );
//    }
//
//    private record OnlineCourseSeed(
//            String title,
//            String slug,
//            String shortDescription,
//            String description,
//            CourseLevel level,
//            OnlineCourseUnlockStrategy unlockStrategy,
//            Integer estimatedDurationInMinutes,
//            List<ModuleSeed> modules
//    ) {
//    }
//
//    private record ModuleSeed(String title, String description, List<LessonSeed> lessons) {
//    }
//
//    private record LessonSeed(String title, String description, String content, int durationInMinutes) {
//    }
//}
