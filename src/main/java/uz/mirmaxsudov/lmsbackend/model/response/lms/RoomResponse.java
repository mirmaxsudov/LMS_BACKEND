package uz.mirmaxsudov.lmsbackend.model.response.lms;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.RoomStatus;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.RoomType;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
public class RoomResponse {
    private UUID id;
    private String name;
    private Integer capacity;
    private RoomType roomType;
    private RoomStatus status;
    private Integer floor;
    private String building;
    private boolean hasProjector;
    private boolean hasComputers;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
