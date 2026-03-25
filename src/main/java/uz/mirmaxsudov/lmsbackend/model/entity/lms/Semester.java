package uz.mirmaxsudov.lmsbackend.model.entity.lms;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.*;
import uz.mirmaxsudov.lmsbackend.model.entity.base.BaseEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Semester extends BaseEntity {
    private String name;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private boolean isActive;

    @OneToMany
    private List<Group> groups = new ArrayList<>();
}