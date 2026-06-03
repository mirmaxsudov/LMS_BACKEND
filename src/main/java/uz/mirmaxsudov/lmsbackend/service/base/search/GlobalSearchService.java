package uz.mirmaxsudov.lmsbackend.service.base.search;

import org.springframework.http.ResponseEntity;
import uz.mirmaxsudov.lmsbackend.model.enums.search.SearchResultType;
import uz.mirmaxsudov.lmsbackend.model.response.ApiPaginateResponse;
import uz.mirmaxsudov.lmsbackend.model.response.search.GlobalSearchResultResponse;

import java.util.List;
import java.util.Set;

public interface GlobalSearchService {
    ResponseEntity<ApiPaginateResponse<List<GlobalSearchResultResponse>>> search(
            String query,
            Set<SearchResultType> types,
            int page,
            int size
    );
}
