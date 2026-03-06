package com.scientia.mercatus.util;

import com.github.slugify.Slugify;

import com.scientia.mercatus.exception.BusinessException;
import com.scientia.mercatus.exception.ErrorEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.function.BiFunction;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class SlugUtil {

    private static final int MAX_SLUG_GENERATION = 5;

    private final Slugify slugify  =  Slugify.builder().underscoreSeparator(false).build();

    public String baseSlug(String input) {
        return slugify.slugify(input.trim());
    }

    public String slugWithId(String input, Long id) {

        return baseSlug(input) + "-" + id;
    }



    public String generateSlugForCreate(String input, Function<String, Boolean> exists){
        String baseSlug = baseSlug(input);
        String generatedSlug = baseSlug;
        for (int attempt = 1; attempt <= MAX_SLUG_GENERATION; attempt++) {
            if (!exists.apply(generatedSlug)){
                return generatedSlug;
            }
            generatedSlug = baseSlug + "-" + attempt;
        }
        throw new BusinessException(ErrorEnum.SLUG_NOT_UNIQUE, "Unable to generate unique slug.");
    }

    public String generateSlugForUpdate(String input, Long id, BiFunction<String, Long, Boolean> exists){
        String baseSlug = baseSlug(input);
        String generatedSlug = baseSlug;
        for (int attempt = 1; attempt <= MAX_SLUG_GENERATION; attempt++) {
            if (!exists.apply(generatedSlug, id)){
                return generatedSlug;
            }
            generatedSlug = baseSlug + "-" + attempt;
        }
        throw new BusinessException(ErrorEnum.SLUG_NOT_UNIQUE, "Unable to generate unique slug.");
    }
}
