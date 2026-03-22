package uz.mirmaxsudov.lmsbackend.repository.user.specification.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.TeacherPosition;

@Getter
@Setter
@Builder
public class TeacherProfileFilter {
    private String search;
    private TeacherPosition position;
}