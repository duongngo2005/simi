package com.ndd.simi_be.tag.service;

import com.ndd.simi_be.common.exception.ConflictException;
import com.ndd.simi_be.common.exception.ResourceNotFoundException;
import com.ndd.simi_be.common.utils.SlugUtils;
import com.ndd.simi_be.tag.dto.CreateTagRequest;
import com.ndd.simi_be.tag.dto.TagResponse;
import com.ndd.simi_be.tag.dto.UpdateTagRequest;
import com.ndd.simi_be.tag.entity.Tag;
import com.ndd.simi_be.tag.mapper.TagMapper;
import com.ndd.simi_be.tag.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TagService {
    private final TagRepository tagRepository;

    @Transactional
    public TagResponse createTag(CreateTagRequest request){
        if (tagRepository.existsByNameIgnoreCase(request.getName())){
            throw new ConflictException("Tag đã tồn tại");
        }

        String slug = SlugUtils.toSlug(request.getName());
        if (tagRepository.existsBySlug(slug)){
            throw new ConflictException("Tag trùng slug");
        }

        Tag tag = Tag.builder()
                .active(true)
                .name(request.getName())
                .slug(SlugUtils.toSlug(request.getName()))
                .build();
        tagRepository.save(tag);

        return TagMapper.toTagResponse(tag);
    }

    @Transactional
    public List<Tag> createTagOrFind(List<String> requests) {
        List<Tag> tags = new ArrayList<>();

        for (String tagName : requests) {
            String slug = SlugUtils.toSlug(tagName.trim());

            Tag tag = tagRepository.findByNameIgnoreCase(tagName.trim())
                    .orElseGet(() -> tagRepository.findBySlug(slug)
                            .orElseGet(() -> {
                                return tagRepository.save(Tag.builder()
                                        .name(tagName.trim())
                                        .active(true)
                                        .slug(slug)
                                        .build());
                            }));
            tags.add(tag);
        }
        return tags;
    }

    @Transactional
    public TagResponse updateTag(UpdateTagRequest request, Long id){
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tag"));



        if (request.getName() != null){
            if (tagRepository.existsByNameIgnoreCase(request.getName())){
                throw new ConflictException("Tên tag đã bị trùng");
            }
            tag.setName(request.getName());
            tag.setSlug(SlugUtils.toSlug(request.getName()));
        }

        if (request.getActive() != null){
            tag.setActive(request.getActive());
        }

        return TagMapper.toTagResponse(tag);
    }

    @Transactional(readOnly = true)
    public List<TagResponse> getAllTags(){
        return tagRepository.findAll().stream().map(TagMapper::toTagResponse).toList();
    }

    @Transactional
    public void softDeleteTag(Long id){
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tag"));

        tag.setActive(false);
    }
}
