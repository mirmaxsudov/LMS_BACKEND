package uz.mirmaxsudov.lmsbackend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uz.mirmaxsudov.lmsbackend.common.util.APIUtil;
import uz.mirmaxsudov.lmsbackend.model.enums.search.SearchResultType;
import uz.mirmaxsudov.lmsbackend.model.response.ApiPaginateResponse;
import uz.mirmaxsudov.lmsbackend.model.response.search.GlobalSearchResultResponse;
import uz.mirmaxsudov.lmsbackend.service.base.search.GlobalSearchService;

import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping(APIUtil.API_BASE_URL + "search")
public class SearchController {
    private static final String ALL_ROLES = "hasAnyRole('SUPER_ADMIN','ADMIN','TEACHER','STUDENT','PARENT','GUARDIAN','MAINTAINER','SUPPORT_TEACHER')";

    private final GlobalSearchService searchService;

    @GetMapping
    @PreAuthorize(ALL_ROLES)
    public ResponseEntity<ApiPaginateResponse<List<GlobalSearchResultResponse>>> search(
            @RequestParam("query") String query,
            @RequestParam(value = "types", required = false) Set<SearchResultType> types,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "8") int size
    ) {
        return searchService.search(query, types, page, size);
    }
}
