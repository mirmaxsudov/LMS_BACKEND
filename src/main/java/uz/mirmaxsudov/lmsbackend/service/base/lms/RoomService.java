package uz.mirmaxsudov.lmsbackend.service.base.lms;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.Room;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.RoomStatus;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.RoomType;
import uz.mirmaxsudov.lmsbackend.model.request.lms.RoomChangeStatusRequest;
import uz.mirmaxsudov.lmsbackend.model.request.lms.RoomCreateRequest;
import uz.mirmaxsudov.lmsbackend.model.request.lms.RoomUpdateRequest;
import uz.mirmaxsudov.lmsbackend.model.response.ApiPaginateResponse;
import uz.mirmaxsudov.lmsbackend.model.response.ApiResponse;
import uz.mirmaxsudov.lmsbackend.model.response.lms.RoomResponse;
import uz.mirmaxsudov.lmsbackend.service.base.BaseCRUDService;

import java.util.List;
import java.util.UUID;

public interface RoomService extends BaseCRUDService<Room> {

    ResponseEntity<ApiPaginateResponse<List<RoomResponse>>> getAll(
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
    );

    ResponseEntity<ApiResponse<RoomResponse>> getByIdResponse(UUID id);

    ResponseEntity<ApiResponse<RoomResponse>> createRoom(@Valid RoomCreateRequest request);

    ResponseEntity<ApiResponse<RoomResponse>> updateRoom(UUID id, @Valid RoomUpdateRequest request);

    ResponseEntity<ApiResponse<RoomResponse>> changeStatus(UUID id, @Valid RoomChangeStatusRequest request);

    ResponseEntity<ApiResponse<Void>> deleteRoom(UUID id);
}
