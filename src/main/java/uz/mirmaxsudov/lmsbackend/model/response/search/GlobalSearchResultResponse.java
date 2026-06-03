package uz.mirmaxsudov.lmsbackend.model.response.search;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.mirmaxsudov.lmsbackend.model.enums.search.SearchResultType;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GlobalSearchResultResponse {
    private UUID id;
    private SearchResultType type;
    private String title;
    private String subtitle;
    private String description;
    private String url;
    private int score;
}
