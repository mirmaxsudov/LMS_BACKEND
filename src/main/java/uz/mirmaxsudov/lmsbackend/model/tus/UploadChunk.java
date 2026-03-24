package uz.mirmaxsudov.lmsbackend.model.tus;

public record UploadChunk(String objectKey, long offset, long size) {
}