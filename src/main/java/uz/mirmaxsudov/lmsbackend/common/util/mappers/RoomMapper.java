package uz.mirmaxsudov.lmsbackend.common.util.mappers;

import uz.mirmaxsudov.lmsbackend.model.entity.lms.Room;
import uz.mirmaxsudov.lmsbackend.model.response.lms.RoomResponse;

public final class RoomMapper {

    private RoomMapper() {}

    public static RoomResponse toResponse(Room room) {
        if (room == null) return null;

        return RoomResponse.builder()
                .id(room.getId())
                .name(room.getName())
                .capacity(room.getCapacity())
                .roomType(room.getRoomType())
                .status(room.getStatus())
                .floor(room.getFloor())
                .building(room.getBuilding())
                .hasProjector(room.isHasProjector())
                .hasComputers(room.isHasComputers())
                .description(room.getDescription())
                .createdAt(room.getCreatedAt())
                .updatedAt(room.getUpdatedAt())
                .build();
    }
}
