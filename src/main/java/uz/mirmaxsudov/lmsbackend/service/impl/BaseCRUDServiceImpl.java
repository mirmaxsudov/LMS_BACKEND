package uz.mirmaxsudov.lmsbackend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import uz.mirmaxsudov.lmsbackend.model.entity.base.BaseEntity;
import uz.mirmaxsudov.lmsbackend.service.base.BaseCRUDService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public abstract class BaseCRUDServiceImpl<T extends BaseEntity, R extends JpaRepository<T, UUID>> implements BaseCRUDService<T> {
    protected final R repository;

    @Override
    public T create(T entity) {
        return repository.save(entity);
    }

    @Override
    public Optional<T> getById(UUID id) {
        return repository.findById(id)
                .filter(entity -> !entity.isDeleted());
    }

    @Override
    public List<T> getAll() {
        return repository.findAll().stream()
                .filter(entity -> !entity.isDeleted())
                .toList();
    }

    @Override
    public T update(UUID id, T entity) {
        return repository.findById(id)
                .filter(existing -> !existing.isDeleted())
                .map(existing -> {
                    entity.setId(id);
                    entity.setCreatedAt(existing.getCreatedAt());
                    return repository.save(entity);
                })
                .orElseThrow(() -> new RuntimeException("Entity not found"));
    }

    @Override
    public void delete(UUID id) {
        repository.deleteById(id);
    }

    @Override
    public void softDelete(UUID id) {
        repository.findById(id)
                .filter(entity -> !entity.isDeleted())
                .ifPresent(entity -> {
                    entity.setDeleted(true);
                    entity.setDeletedAt(LocalDateTime.now());
                    repository.save(entity);
                });
    }
}
