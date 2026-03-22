package uz.mirmaxsudov.lmsbackend.model.request.user;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class ParentProfileRequest {
    private UUID userId;
    private Set<UUID> studentIds;
}
