package uz.mirmaxsudov.lmsbackend.controller.lms;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uz.mirmaxsudov.lmsbackend.common.util.APIUtil;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.RoomStatus;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.RoomType;
import uz.mirmaxsudov.lmsbackend.model.request.lms.RoomChangeStatusRequest;
import uz.mirmaxsudov.lmsbackend.model.request.lms.RoomCreateRequest;
import uz.mirmaxsudov.lmsbackend.model.request.lms.RoomUpdateRequest;
import uz.mirmaxsudov.lmsbackend.model.response.ApiPaginateResponse;
import uz.mirmaxsudov.lmsbackend.model.response.ApiResponse;
import uz.mirmaxsudov.lmsbackend.model.response.lms.RoomResponse;
import uz.mirmaxsudov.lmsbackend.service.base.lms.RoomService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(APIUtil.API_BASE_URL + "rooms")
public class RoomController {

    private final RoomService roomService;

    @GetMapping
    public ResponseEntity<ApiPaginateResponse<List<RoomResponse>>> getAll(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "roomType", required = false) RoomType roomType,
            @RequestParam(value = "status", required = false) RoomStatus status,
            @RequestParam(value = "building", required = false) String building,
            @RequestParam(value = "floor", required = false) Integer floor,
            @RequestParam(value = "minCapacity", required = false) Integer minCapacity,
            @RequestParam(value = "maxCapacity", required = false) Integer maxCapacity,
            @RequestParam(value = "hasProjector", required = false) Boolean hasProjector,
            @RequestParam(value = "hasComputers", required = false) Boolean hasComputers
    ) {
        return roomService.getAll(
                page, size, search, roomType, status,
                building, floor, minCapacity, maxCapacity,
                hasProjector, hasComputers
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RoomResponse>> getById(@PathVariable UUID id) {
        return roomService.getByIdResponse(id);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<RoomResponse>> create(@RequestBody @Valid RoomCreateRequest request) {
        return roomService.createRoom(request);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RoomResponse>> update(
            @PathVariable UUID id,
            @RequestBody @Valid RoomUpdateRequest request
    ) {
        return roomService.updateRoom(id, request);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<RoomResponse>> changeStatus(
            @PathVariable UUID id,
            @RequestBody @Valid RoomChangeStatusRequest request
    ) {
        return roomService.changeStatus(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        return roomService.deleteRoom(id);
    }
}
