package com.ndd.simi_be.tag.mapper;

import com.ndd.simi_be.tag.dto.TagResponse;
import com.ndd.simi_be.tag.entity.Tag;

public class TagMapper {
    public static TagResponse toTagResponse(Tag tag){
        return TagResponse.builder()
                .id(tag.getId())
                .name(tag.getName())
                .slug(tag.getSlug())
                .active(tag.isActive())
                .build();
    }
}
