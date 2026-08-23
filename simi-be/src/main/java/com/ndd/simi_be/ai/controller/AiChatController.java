package com.ndd.simi_be.ai.controller;

import com.ndd.simi_be.ai.dto.request.AiChatRequest;
import com.ndd.simi_be.ai.dto.response.AiChatResponse;
import com.ndd.simi_be.ai.service.AiChatService;
import com.ndd.simi_be.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ai")
public class AiChatController {
    private final AiChatService aiChatService;
    @PostMapping("/chat")
    public ResponseEntity<ApiResponse<AiChatResponse>> chat(
            @Valid @RequestBody AiChatRequest request
    ) {
        ApiResponse<AiChatResponse> response = ApiResponse.<AiChatResponse>builder().body(aiChatService.chat(request)).build();
        return ResponseEntity.ok(response);
    }
}
