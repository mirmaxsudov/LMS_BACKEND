package uz.mirmaxsudov.lmsbackend.service.impl.lms;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uz.mirmaxsudov.lmsbackend.common.filter.PageableBuilder;
import uz.mirmaxsudov.lmsbackend.common.util.mappers.RoomMapper;
import uz.mirmaxsudov.lmsbackend.exceptions.CustomBadRequestException;
import uz.mirmaxsudov.lmsbackend.exceptions.CustomConflictException;
import uz.mirmaxsudov.lmsbackend.exceptions.CustomNotFoundException;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.Room;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.RoomStatus;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.RoomType;
import uz.mirmaxsudov.lmsbackend.model.request.lms.RoomChangeStatusRequest;
import uz.mirmaxsudov.lmsbackend.model.request.lms.RoomCreateRequest;
import uz.mirmaxsudov.lmsbackend.model.request.lms.RoomUpdateRequest;
import uz.mirmaxsudov.lmsbackend.model.response.ApiPaginateResponse;
import uz.mirmaxsudov.lmsbackend.model.response.ApiResponse;
import uz.mirmaxsudov.lmsbackend.model.response.lms.RoomResponse;
import uz.mirmaxsudov.lmsbackend.repository.lms.room.RoomFilter;
import uz.mirmaxsudov.lmsbackend.repository.lms.room.RoomRepository;
import uz.mirmaxsudov.lmsbackend.repository.lms.room.RoomSpecification;
import uz.mirmaxsudov.lmsbackend.service.base.lms.RoomService;
import uz.mirmaxsudov.lmsbackend.service.impl.BaseCRUDServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class RoomServiceImpl extends BaseCRUDServiceImpl<Room, RoomRepository> implements RoomService {

    public RoomServiceImpl(RoomRepository repository) {
        super(repository);
    }

    @Override
    public ResponseEntity<ApiPaginateResponse<List<RoomResponse>>> getAll(
            int page,
            int size,
            String search,
            RoomType roomType,
            RoomStatus status,
            String building,
            Integer floor,
            Integer minCapacity,
            Integer maxCapacity,
            Boolean hasProjector,
            Boolean hasComputers
    ) {
        int normalizedPage = Math.max(page - 1, 0);
        int normalizedSize = size <= 0 ? 10 : size;
        validateCapacityRange(minCapacity, maxCapacity);

        Pageable pageable = PageableBuilder.build(normalizedPage, normalizedSize);

        Specification<Room> filter = RoomSpecification.filter(RoomFilter.builder()
                .search(search)
                .roomType(roomType)
                .status(status)
                .building(building)
                .floor(floor)
                .minCapacity(minCapacity)
                .maxCapacity(maxCapacity)
                .hasProjector(hasProjector)
                .hasComputers(hasComputers)
                .build());

        Page<Room> rooms = repository.findAll(filter, pageable);
        List<RoomResponse> results = rooms.getContent().stream()
                .map(RoomMapper::toResponse)
                .toList();

        return ResponseEntity.ok(ApiPaginateResponse.<List<RoomResponse>>builder()
                .success(true)
                .message("Rooms fetched successfully")
                .results(results)
                .total((int) rooms.getTotalElements())
                .page(rooms.getNumber() + 1)
                .size(rooms.getSize())
                .hasNext(rooms.hasNext())
                .build());
    }

    @Override
    public ResponseEntity<ApiResponse<RoomResponse>> getByIdResponse(UUID id) {
        Room room = findActiveRoom(id);

        return ResponseEntity.ok(ApiResponse.<RoomResponse>builder()
                .success(true)
                .message("Room fetched successfully")
                .data(RoomMapper.toResponse(room))
                .build());
    }

    @Override
    public ResponseEntity<ApiResponse<RoomResponse>> createRoom(RoomCreateRequest request) {
        String normalizedName = normalizeName(request.getName());
        validateDuplicateName(normalizedName, null);

        Room room = Room.builder()
                .name(normalizedName)
                .capacity(request.getCapacity())
                .roomType(request.getRoomType())
                .status(RoomStatus.ACTIVE)
                .floor(request.getFloor())
                .building(request.getBuilding() != null ? request.getBuilding().trim() : null)
                .hasProjector(Boolean.TRUE.equals(request.getHasProjector()))
                .hasComputers(Boolean.TRUE.equals(request.getHasComputers()))
                .description(request.getDescription())
                .build();

        Room savedRoom = repository.save(room);

        return ResponseEntity.ok(ApiResponse.<RoomResponse>builder()
                .success(true)
                .message("Room created successfully")
                .data(RoomMapper.toResponse(savedRoom))
                .build());
    }

    @Override
    public ResponseEntity<ApiResponse<RoomResponse>> updateRoom(UUID id, RoomUpdateRequest request) {
        Room existingRoom = findActiveRoom(id);

        String normalizedName = normalizeName(request.getName());
        validateDuplicateName(normalizedName, existingRoom.getId());

        existingRoom.setName(normalizedName);
        existingRoom.setCapacity(request.getCapacity());
        existingRoom.setRoomType(request.getRoomType());
        existingRoom.setStatus(request.getStatus());
        existingRoom.setFloor(request.getFloor());
        existingRoom.setBuilding(request.getBuilding() != null ? request.getBuilding().trim() : null);
        existingRoom.setHasProjector(Boolean.TRUE.equals(request.getHasProjector()));
        existingRoom.setHasComputers(Boolean.TRUE.equals(request.getHasComputers()));
        existingRoom.setDescription(request.getDescription());

        Room updatedRoom = repository.save(existingRoom);

        return ResponseEntity.ok(ApiResponse.<RoomResponse>builder()
                .success(true)
                .message("Room updated successfully")
                .data(RoomMapper.toResponse(updatedRoom))
                .build());
    }

    @Override
    public ResponseEntity<ApiResponse<RoomResponse>> changeStatus(UUID id, RoomChangeStatusRequest request) {
        Room room = findActiveRoom(id);

        if (room.getStatus() == request.getStatus())
            throw new CustomConflictException("Room is already in status: " + request.getStatus());

        room.setStatus(request.getStatus());
        Room updatedRoom = repository.save(room);

        return ResponseEntity.ok(ApiResponse.<RoomResponse>builder()
                .success(true)
                .message("Room status changed successfully")
                .data(RoomMapper.toResponse(updatedRoom))
                .build());
    }

    @Override
    public ResponseEntity<ApiResponse<Void>> deleteRoom(UUID id) {
        Room room = findActiveRoom(id);

        room.setDeleted(true);
        room.setDeletedAt(LocalDateTime.now());
        repository.save(room);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Room deleted successfully")
                .build());
    }

    private Room findActiveRoom(UUID id) {
        return repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new CustomNotFoundException("Room not found with id: " + id));
    }

    private String normalizeName(String name) {
        if (name == null)
            throw new CustomBadRequestException("Room name is required");

        String trimmed = name.trim();
        if (trimmed.isBlank())
            throw new CustomBadRequestException("Room name is required");

        return trimmed;
    }

    private void validateDuplicateName(String name, UUID excludedId) {
        boolean exists = excludedId == null
                ? repository.existsByNameIgnoreCaseAndDeletedFalse(name)
                : repository.existsByNameIgnoreCaseAndIdNotAndDeletedFalse(name, excludedId);

        if (exists)
            throw new CustomConflictException("Room with name '" + name + "' already exists");
    }

    private void validateCapacityRange(Integer minCapacity, Integer maxCapacity) {
        if (minCapacity != null && minCapacity <= 0)
            throw new CustomBadRequestException("minCapacity must be greater than 0");

        if (maxCapacity != null && maxCapacity <= 0)
            throw new CustomBadRequestException("maxCapacity must be greater than 0");

        if (minCapacity != null && maxCapacity != null && minCapacity > maxCapacity)
            throw new CustomBadRequestException("minCapacity must be less than or equal to maxCapacity");
    }
}
