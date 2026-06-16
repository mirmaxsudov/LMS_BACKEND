package uz.mirmaxsudov.lmsbackend.model.entity.lms;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.mirmaxsudov.lmsbackend.model.entity.base.BaseEntity;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.RoomStatus;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.RoomType;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "rooms")
public class Room extends BaseEntity {

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String name;

    @NotNull
    @Positive
    @Column(nullable = false)
    private Integer capacity;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "room_type", nullable = false)
    private RoomType roomType;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomStatus status;

    @Column
    private Integer floor;

    @Size(max = 100)
    @Column(length = 100)
    private String building;

    @Builder.Default
    @Column(name = "has_projector", nullable = false)
    private boolean hasProjector = false;

    @Builder.Default
    @Column(name = "has_computers", nullable = false)
    private boolean hasComputers = false;

    @Size(max = 500)
    @Column(length = 500)
    private String description;
}
