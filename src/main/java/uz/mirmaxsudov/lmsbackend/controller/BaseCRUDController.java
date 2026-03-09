package uz.mirmaxsudov.lmsbackend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.mirmaxsudov.lmsbackend.model.entity.base.BaseEntity;
import uz.mirmaxsudov.lmsbackend.model.response.ApiResponse;
import uz.mirmaxsudov.lmsbackend.service.base.BaseCRUDService;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public abstract class BaseCRUDController<T extends BaseEntity, S extends BaseCRUDService<T>> {
    protected final S service;

    @PostMapping
    public ResponseEntity<ApiResponse<T>> create(@RequestBody T entity) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Created successfully", service.create(entity)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<T>> getById(@PathVariable UUID id) {
        return service.getById(id).map(entity -> ResponseEntity.ok(new ApiResponse<>(true, "Retrieved successfully", entity))).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<T>>> getAll() {
        return ResponseEntity.ok(new ApiResponse<>(true, "Retrieved all successfully", service.getAll()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<T>> update(@PathVariable UUID id, @RequestBody T entity) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Updated successfully", service.update(id, entity)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        service.softDelete(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Deleted successfully", null));
    }
}