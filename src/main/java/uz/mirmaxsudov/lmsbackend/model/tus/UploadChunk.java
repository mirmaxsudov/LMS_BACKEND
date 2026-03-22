package uz.mirmaxsudov.lmsbackend.model.tus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UploadChunk {
    private final String objectKey;
    private final long offset;
    private final long size;
}
