package uz.mirmaxsudov.lmsbackend.repository.user.specification.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.StudentStatus;

@Getter
@Setter
@Builder
public class StudentProfileFilter {
    private String search;
    private StudentStatus status;
}
