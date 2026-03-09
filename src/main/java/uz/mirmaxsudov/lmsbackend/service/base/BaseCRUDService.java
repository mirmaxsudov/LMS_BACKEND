package uz.mirmaxsudov.lmsbackend.service.base;

import uz.mirmaxsudov.lmsbackend.model.entity.base.BaseEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BaseCRUDService<T extends BaseEntity> {
    T create(T entity);

    Optional<T> getById(UUID id);

    List<T> getAll();

    T update(UUID id, T entity);

    void delete(UUID id);

    void softDelete(UUID id);
}