package uz.mirmaxsudov.lmsbackend.repository.lms.room;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.RoomStatus;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.RoomType;

@Getter
@Setter
@Builder
public class RoomFilter {
    private String search;
    private RoomType roomType;
    private RoomStatus status;
    private String building;
    private Integer floor;
    private Integer minCapacity;
    private Integer maxCapacity;
    private Boolean hasProjector;
    private Boolean hasComputers;
}
