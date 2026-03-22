package uz.mirmaxsudov.lmsbackend.model.request.auth;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class PermissionRequest {
    private UUID id;
}