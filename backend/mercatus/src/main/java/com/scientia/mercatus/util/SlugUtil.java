package com.scientia.mercatus.util;

import com.github.slugify.Slugify;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SlugUtil {

    private final Slugify slugify  =  Slugify.builder().underscoreSeparator(false).build();

    public String baseSlug(String input) {
        return slugify.slugify(input.trim());
    }

    public String slugWithId(String input, Long id) {
        return baseSlug(input) + "-" + id;
    }

}
