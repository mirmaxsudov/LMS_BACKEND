package uz.mirmaxsudov.lmsbackend.repository.lms.room;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.Room;

import java.util.ArrayList;
import java.util.List;

public class RoomSpecification {

    public static Specification<Room> filter(RoomFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("deleted"), Boolean.FALSE));

            if (filter.getRoomType() != null)
                predicates.add(cb.equal(root.get("roomType"), filter.getRoomType()));

            if (filter.getStatus() != null)
                predicates.add(cb.equal(root.get("status"), filter.getStatus()));

            if (filter.getBuilding() != null && !filter.getBuilding().isBlank())
                predicates.add(cb.equal(cb.lower(root.get("building")), filter.getBuilding().toLowerCase().trim()));

            if (filter.getFloor() != null)
                predicates.add(cb.equal(root.get("floor"), filter.getFloor()));

            if (filter.getMinCapacity() != null)
                predicates.add(cb.greaterThanOrEqualTo(root.get("capacity"), filter.getMinCapacity()));

            if (filter.getMaxCapacity() != null)
                predicates.add(cb.lessThanOrEqualTo(root.get("capacity"), filter.getMaxCapacity()));

            if (filter.getHasProjector() != null)
                predicates.add(cb.equal(root.get("hasProjector"), filter.getHasProjector()));

            if (filter.getHasComputers() != null)
                predicates.add(cb.equal(root.get("hasComputers"), filter.getHasComputers()));

            if (filter.getSearch() != null && !filter.getSearch().isBlank()) {
                String pattern = "%" + filter.getSearch().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("name")), pattern),
                        cb.like(cb.lower(root.get("building")), pattern),
                        cb.like(cb.lower(root.get("description")), pattern)
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
