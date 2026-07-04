package com.ndd.simi_be.tag.controller;

import com.ndd.simi_be.common.response.ApiResponse;
import com.ndd.simi_be.tag.dto.CreateTagRequest;
import com.ndd.simi_be.tag.dto.TagResponse;
import com.ndd.simi_be.tag.dto.UpdateTagRequest;
import com.ndd.simi_be.tag.service.TagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tags")
public class TagController {
    private final TagService tagService;

    @PostMapping
    public ResponseEntity<ApiResponse<TagResponse>> createTag(@Valid @RequestBody CreateTagRequest request){
        ApiResponse<TagResponse> apiResponse = ApiResponse.<TagResponse>builder()
                .status(201)
                .message("Tạo tag thành công")
                .body(tagService.createTag(request))
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<TagResponse>> updateTag(
            @RequestBody UpdateTagRequest request,
            @PathVariable Long id
    ){
        ApiResponse<TagResponse> apiResponse = ApiResponse.<TagResponse>builder()
                .status(200)
                .message("Cập nhật tag thành công")
                .body(tagService.updateTag(request, id))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TagResponse>>> getAllTags(){
        ApiResponse<List<TagResponse>> apiResponse = ApiResponse.<List<TagResponse>>builder()
                .status(200)
                .body(tagService.getAllTags())
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> softDeleteTag(@PathVariable Long id){
        tagService.softDeleteTag(id);
        return ResponseEntity.noContent().build();
    }
}
