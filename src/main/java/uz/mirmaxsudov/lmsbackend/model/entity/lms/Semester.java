package uz.mirmaxsudov.lmsbackend.model.entity.lms;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import uz.mirmaxsudov.lmsbackend.model.entity.base.BaseEntity;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.SemesterStatus;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Semester extends BaseEntity {
    private String name;
    private String description;
    private int startYear;
    private int endYear;
    private boolean isActive;
    @Enumerated(EnumType.STRING)
    private SemesterStatus status;
}