package uz.mirmaxsudov.lmsbackend.common.util;

import org.springframework.data.domain.Pageable;

public interface PageUtil {
    static Pageable getPageable(int page, int size) {
        return Pageable.ofSize(size).withPage(page);
    }
}
