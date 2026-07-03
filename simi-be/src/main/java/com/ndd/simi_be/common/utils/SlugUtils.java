package com.ndd.simi_be.common.utils;

import com.github.slugify.Slugify;

public final class SlugUtils {
    private static final Slugify slugify = Slugify.builder()
            .customReplacement("Đ", "d")
            .customReplacement("đ", "d")
            .lowerCase(true)
            .build();
    public SlugUtils(){}
    public static String toSlug(String input){
        return slugify.slugify(input);
    }
}
