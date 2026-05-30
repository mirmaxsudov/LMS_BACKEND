package uz.mirmaxsudov.lmsbackend.service.base;

import org.springframework.web.multipart.MultipartFile;
import uz.mirmaxsudov.lmsbackend.model.entity.auth.User;
import uz.mirmaxsudov.lmsbackend.model.entity.content.Attachment;
import uz.mirmaxsudov.lmsbackend.model.enums.content.AttachmentType;
import uz.mirmaxsudov.lmsbackend.model.response.content.AttachmentDownload;
import uz.mirmaxsudov.lmsbackend.model.response.content.AttachmentResponse;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AttachmentService {
    Attachment upload(MultipartFile file, AttachmentType type, User uploadedBy);

    List<AttachmentResponse> uploadMany(List<MultipartFile> files, AttachmentType type, User uploadedBy);

    AttachmentResponse toResponse(Attachment attachment);

    AttachmentDownload download(UUID id);

    void delete(UUID id);

    Optional<Attachment> getOptionalById(UUID id);
}
