package uz.mirmaxsudov.lmsbackend.repository.user.specification.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ParentProfileFilter {
    private String search;
}
