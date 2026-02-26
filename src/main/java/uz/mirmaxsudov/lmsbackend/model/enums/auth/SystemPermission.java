package uz.mirmaxsudov.lmsbackend.model.enums.auth;

public enum SystemPermission {
    // USER
    USER_CREATE,
    USER_EDIT,
    USER_DELETE,
    USER_VIEW,

    // COURSE
    COURSE_CREATE,
    COURSE_EDIT,
    COURSE_DELETE,
    COURSE_VIEW,

    // QUIZ
    QUIZ_CREATE,
    QUIZ_EDIT,
    QUIZ_DELETE,
    QUIZ_START,
    QUIZ_EVALUATE,

    // FILE
    FILE_UPLOAD,
    FILE_DELETE,
    FILE_VIEW
}
