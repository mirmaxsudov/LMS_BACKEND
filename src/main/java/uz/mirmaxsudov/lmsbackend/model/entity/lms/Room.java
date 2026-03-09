package uz.mirmaxsudov.lmsbackend.model.entity.lms;

import jakarta.persistence.Entity;
import lombok.*;
import uz.mirmaxsudov.lmsbackend.model.entity.base.BaseEntity;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Room extends BaseEntity {
    private String name;
    private String description;
    private int capacity;
    private int number;
    private String building;
    private int floor;
}
