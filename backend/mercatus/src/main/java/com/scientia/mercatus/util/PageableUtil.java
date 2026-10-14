package com.scientia.mercatus.util;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class PageableUtil {

    @Value("${pagination.max-size:10}")
    private static int maxPageSize;

    public static Pageable getSafePageable(Pageable pageable) {
        int size = Math.min(pageable.getPageSize(), maxPageSize);
        Sort sort = pageable.getSort().isSorted()
                ? pageable.getSort() : Sort.by(Sort.Direction.DESC, "createdAt");
        return PageRequest.of(
                pageable.getPageNumber(),
                size,
                sort
        );
    }
}
