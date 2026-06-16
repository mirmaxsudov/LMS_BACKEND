package uz.mirmaxsudov.lmsbackend.model.request.lms;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.RoomStatus;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.RoomType;

@Getter
@Setter
public class RoomUpdateRequest {

    @NotBlank(message = "Room name is required")
    @Size(max = 100, message = "Room name must not exceed 100 characters")
    private String name;

    @NotNull(message = "Capacity is required")
    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer capacity;

    @NotNull(message = "Room type is required")
    private RoomType roomType;

    @NotNull(message = "Status is required")
    private RoomStatus status;

    private Integer floor;

    @Size(max = 100, message = "Building name must not exceed 100 characters")
    private String building;

    private Boolean hasProjector = false;

    private Boolean hasComputers = false;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;
}
