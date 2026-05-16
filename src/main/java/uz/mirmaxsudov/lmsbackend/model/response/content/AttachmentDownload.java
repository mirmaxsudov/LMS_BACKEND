package uz.mirmaxsudov.lmsbackend.model.response.content;

import java.io.InputStream;

public record AttachmentDownload(
        InputStream stream,
        long size,
        String contentType,
        String fileName
) {
}
