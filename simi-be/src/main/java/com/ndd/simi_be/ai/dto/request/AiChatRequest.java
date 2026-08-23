package com.ndd.simi_be.ai.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiChatRequest {
    @NotBlank(message = "Tin nhắn không được để trống")
    @Size(max = 500, message = "Tin nhắn tối đa 500 ký tự")
    private String message;

    @Valid
    @Size(max = 6, message = "Lịch sử hội thoại tối đa 6 lượt gần nhất")
    @Builder.Default
    private List<AiChatTurn> history = new ArrayList<>();
}