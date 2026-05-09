package uz.mirmaxsudov.lmsbackend.repository.content;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.mirmaxsudov.lmsbackend.model.entity.content.Attachment;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, UUID> {
    Optional<Attachment> findByIdAndDeletedFalse(UUID id);
}
